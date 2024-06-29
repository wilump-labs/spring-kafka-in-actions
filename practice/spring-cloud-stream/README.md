# Spring Cloud Stream Quick Start

```yaml
spring:
  cloud:
    function:
      definition: myProducer;myConsumer
    stream:
      function:
        bindings:
          myProducer-out-0: producer-test
          myConsumer-in-0: consumer-test
      kafka:
        binder:
          brokers: localhost:9092,localhost:9093,localhost:9094
          auto-create-topics: false
          required-acks: 0
          configuration:
            key.serializer: org.apache.kafka.common.serialization.StringSerializer
        bindings:
          consumer-test:
            consumer:
              start-offset: latest
      bindings:
        producer-test:
          destination: wilump.json
          contentType: application/json
        consumer-test:
          group: test-consumer-group
          destination: wilump.json
          consumer:
            concurrency: 1
```
- `spring.cloud.function.definition` : Bean 정의
- `spring.cloud.stream.function.bindings` : Bean과 채널에 바인딩


- `spring.cloud.stream.kafka.binder` : Kafka binder 공통 설정
- `spring.cloud.stream.bindings` : output, input 채널에 대한 공통 설정
- `spring.cloud.stream.kafka.bindings` : output, input 채널에 대한 카프카 특화 설정
