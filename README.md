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