package labs.wilump.kafka.practice.consumer

import labs.wilump.kafka.practice.logger
import labs.wilump.kafka.practice.model.MyMessage
import labs.wilump.kafka.practice.model.Topic
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class MyConsumer {
    private val log = logger()

    init {
        log.info("MyConsumer initialized")
    }

    @KafkaListener(
        topics = [Topic.JSON_TOPIC],
        groupId = "test-consumer-group",
    )
    fun accept(message: ConsumerRecord<String, MyMessage>) {
        log.info("Received message: ${message.value()}")
    }
}