package labs.wilump.kafka.practice.consumer

import labs.wilump.kafka.practice.logger
import labs.wilump.kafka.practice.model.MyMessage
import labs.wilump.kafka.practice.model.Topic
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Profile("multi-config")
@Component
class MyStringConsumer {
    private val log = logger()

    init {
        log.info("MyStringConsumer initialized")
    }

    @KafkaListener(
        topics = [Topic.STRING_TOPIC],
        groupId = "test-consumer-group",
        containerFactory = "stringKafkaListenerContainerFactory",
    )
    fun accept(message: ConsumerRecord<String, String>) {
        log.info("Received string message: ${message.value()}")
    }
}