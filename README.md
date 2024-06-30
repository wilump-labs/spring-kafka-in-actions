# Spring kafka in actions
## Spring Kafka vs Spring Cloud Stream
### Spring Kafka vs Spring Cloud Stream

|                                                      Spring Kafka                                                      |      비교      |                              Spring Cloud Stream                              |
|:----------------------------------------------------------------------------------------------------------------------:|:------------:|:-----------------------------------------------------------------------------:|
|                                                    **kafka-client**                                                    |     의존성      |                               **spring-kafka**                                |
|                                       ![spring-kafka](./assets/spring-kafka.png)                                       | Config 설정 방법 |           ![spring-cloud-stream](./assets/spring-cloud-stream.png)            |
| Configuration 을 정의하고, producer 또는 comsumer 가 해당 설정을 향하게 함<br><br>별도의 설정이 필요한 경우 새로 더 만들어서 할당<br><br>Configuration을 재활용 |              |         하나의 큰 Configuration을 정의하고, 세부적으로 producer 또는 consumer의 설정을 정의         |
|                                             **비교적 낮음**<br><br>카프카 지향적으로 정의                                             |    추상화 정도    | **비교적 높음**<br><br>카프카 외에도 다양한 메시징 시스템을 지원하기 위한 용어 선택 및 설정(e.g. RabbitMQ, ...) |
|                                                        비교적 적음                                                          |   설정 수정 빈도   |                                    비교적 많음                                     |

<bR>

## Spring kafka
### Configuration 관리
- KafkaConfig를 코드로 관리하는 경우 Auto Configuration 으로 인해 `KafkaProperties` 혹은 `KafkaTemplate`이 정상적으로 Bean으로 등록되지 않을 수 있다
  - 이 때는 KafkaConfig 내 설정 Bean들에 `@Primary` 어노테이션을 추가하여 우선순위를 부여한다

<br>

### 2개 이상의 Configuration 사용
- `KafkaTemplate` 사용 시에는 `@Qualifier` 어노테이션을 통해 사용할 Configuration을 지정한다
- Consumer에 `@KafkaListener` 어노테이션을 사용할 경우 `containerFactory` 속성을 통해 사용할 컨테이너 팩토리를 명시한다

<br>

### BatchListener
- `kafkaListenerFactory`의 설정을 통해 `BatchListener`를 사용할 수 있다
  ```kotlin
  fun kafkaListenerFactory(...) {
    ...
  
    // BatchListener 설정
    factory.isBatchListener = true
    // BatchListener AckMode 설정
    factory.containerProperties.ackMode = ContainerProperties.AckMode.BATCH
  
    ...
  }
  ```
- BatchListener를 사용할 경우 `@KafkaListener` 어노테이션에 `containerFactory` 속성을 추가하여 사용한다

<br>

### 수동커밋
- `consumerFactory`와 `containerFactory`의 설정을 통해 수동커밋을 사용할 수 있다
  ```kotlin
  fun consumerFactory(...) {
    ...
  
    // 수동커밋 설정
    factory.isAutoCommit = false
  
    ...
  }
  
  fun kafkaListenerFactory(...) {
      ...
    
      // 수동커밋 설정
      factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL
  
      ...
  }
  ```


- 수동 커밋 모드를 설정할 때는 `AckMode.MANUAL`와 `AckMode.MANUAL_IMMEDIATE` 중 하나를 사용할 수 있다.
  - `AckMode.MANUAL`
    - `Acknowledgement.acknowledge()` 메서드가 호출되면 다음번 `poll()` 때 커밋을 한다
    - 매번 acknowledge() 메서드를 호출하면 BATCH 옵션과 동일하게 동작한다.
    - `AcknowledgingMessageListener` 또는 `BatchAcknowledgingMessageListener`를 리스너로 사용해야 한다.
  - `AckMode.MANUAL_IMMEDIATE`
    - `Acknowledgement.acknowledge()` 메서드가 호출되면 즉시 커밋을 한다
    - `AcknowledgingMessageListener` 또는 `BatchAcknowledgingMessageListener`를 리스너로 사용해야 한다.

  
- cf. 자동 커밋 defualt 주기(`auto.commit.interval.ms`)는 5초 (5000ms)

<br>

## 리밸런싱
중복 컨슘, 데이터 처리 중복 이슈는 보통 리밸런싱으로 인해 발생한다

### 리밸런싱은 언제 발생하는가?
- 컨슈머 그룹에 새로운 컨슈머가 추가되거나, 기존 컨슈머가 제거된 경우 (**컨슈머 측의 변화**)
- 파티션이 추가되거나 재할당되는 경우 (브로커 측의 변화)

#### 컨슈머 측의 변화
- `session.timeout.ms`와 `heartbeat.interval.ms`
  - heartbeat 가 한동안 broker로 전송되지 않아서 세션 타임아웃이 발생하면 컨슈머 상태는 비정상으로 판단한다
  - 보통은 `session.timeout.ms` 대비 `heartbeat.interval.ms`을 1/3 정도로 설정한다 (세 번의 기회 제공)


- `max.poll.interval.ms`
  - 컨슈머가 한동안 poll()을 호출하지 않으면 컨슈머 상태를 비정상으로 판단한다
  - 너무 무거운 작업을 수행하면 polling interval이 길어져서 비정상으로 판단될 수 있다

### 리밸런싱 대응
- 사실 중복 컨슘, 컨슘 누락 이슈는 컨슈머의 입장에서 발생하는 이슈이다.
  - commit 관점에서는 단 한번의 레코드만 다루게 된다 

- 이를 대비하기 위해서는 리밸런싱 빈도를 줄일 수 있게 적절한 옵션 값을 사용하자
- 추가로 일반적으로 중복이 누락보다는 낫다. 고로 **수동커밋을 활용**하자 (수동커밋은 컨슘 누락은 방지할 수 있다)

<br>

## 멱등성 보장
### EOS(Exactly Once Semantics) - 프로듀서 차원의 멱등성
- EOS를 보장하기 위해서는 producer acks를 반드시 `all`(-1)로 설정해야 한다
  - 메세지를 단 한번 보내는 옵션이기 때문에 팔로워 파티션까지 모두 ack를 받아야 한다
  - 이후 중복된 메세지를 필터링하는 로직을 추가하여 EOS를 보장할 수 있다
- ProducerConfig
  - `acks=all`
  - `enable.idempotence=true`

### 컨슈머 차원의 멱등성
- 컨슈머 내부에 `ConcurrentHashMap`을 정의하여 중복된 메세지를 필터링하는 로직을 추가하여 멱등성을 보장할 수 있다
  - 값의 유무 여부를 판단하여 중복된 메세지를 필터링한다
  - 메모리 외에도 DB, Cache 같은 저장소를 사용하여 중복 메세지를 필터링할 수 있다

<br>

## Container endpoints
```yaml
docker-compose up -d
```

#### Kafka
- http://localhost:9092
- http://localhost:9093
- http://localhost:9094

#### Zookeeper
- http://localhost:2181

#### Monitoring
- kafka-ui: http://localhost:8081
- CMAK: http://localhost:9000
- Kowl: http://localhost:8989

#### Mysql
- http://localhost:13306
  - root password: 1234
  - database: kafka-practice