# Spring kafka Quick Start

```yaml
spring.config.activate.on-profile: auto-config
spring:
  kafka:
    bootstrap-servers: localhost:9092,localhost:9093,localhost:9094
    consumer:
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      auto-offset-reset: latest
      properties:
        spring.json.trusted.packages: "*"
        allow.auto.create.topics: false
    listener:
      concurrency: 1
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      acks: 1
---
spring.config.activate.on-profile: code
spring:
  kafka:
    bootstrap-servers: localhost:9092,localhost:9093,localhost:9094
    consumer:
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      acks: 1
---
spring.config.activate.on-profile: multi-config
spring:
  kafka:
    json:
      bootstrap-servers: localhost:9092,localhost:9093,localhost:9094
      consumer:
        key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
        value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      producer:
        key-serializer: org.apache.kafka.common.serialization.StringSerializer
        value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
        acks: 1
    string:
      bootstrap-servers: localhost:9092,localhost:9093,localhost:9094
      consumer:
        key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
        value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      producer:
        key-serializer: org.apache.kafka.common.serialization.StringSerializer
        value-serializer: org.apache.kafka.common.serialization.StringDeserializer
        acks: 1
```

- 기본적인 Auto Configuration을 사용하는 방법
- 코드로 직접 설정하는 방법
- 여러개의 Configuration을 사용하는 방법
