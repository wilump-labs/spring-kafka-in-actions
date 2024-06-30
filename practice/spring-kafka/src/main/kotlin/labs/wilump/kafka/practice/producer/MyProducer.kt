package labs.wilump.kafka.practice.producer

import labs.wilump.kafka.practice.logger
import labs.wilump.kafka.practice.model.MyMessage
import labs.wilump.kafka.practice.model.Topic
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class MyProducer(
    private val kafkaTemplate: KafkaTemplate<String, Any>,
) {
    private val log = logger()

    init {
        log.info("MyProducer initialized")
    }

    fun sendMessage(message: MyMessage) {
        log.info("Sending message: $message")
        kafkaTemplate.send(
            Topic.JSON_TOPIC,
            message.version.toString(),
            message,
        )
    }
}