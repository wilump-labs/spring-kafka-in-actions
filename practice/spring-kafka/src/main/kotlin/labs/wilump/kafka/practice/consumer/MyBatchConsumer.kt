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
class MyBatchConsumer {
    private val log = logger()

    init {
        log.info("MyBatchConsumer initialized")
    }

    @KafkaListener(
        topics = [Topic.JSON_TOPIC],
        groupId = "batch-test-consumer-group",
        containerFactory = "batchKafkaListenerContainerFactory",
    )
    fun accept(messages: List<ConsumerRecord<String, MyMessage>>) {
        log.info("Arrived batch messages - count: ${messages.size}")
        for (message in messages) {
            log.info("Received batch message: ${message.value()}")
        }
    }
}