package labs.wilump.kafka.practice.producer

import labs.wilump.kafka.practice.logger
import labs.wilump.kafka.practice.model.MyMessage
import labs.wilump.kafka.practice.model.Topic
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Profile("multi-config")
@Component
class MyStringProducer(
    @Qualifier("stringKafkaTemplate")
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {
    private val log = logger()

    init {
        log.info("MyStringProducer initialized")
    }

    fun sendMessageWithKey(key: String, message: String) {
        log.info("Sending string message: $message")
        kafkaTemplate.send(
            Topic.STRING_TOPIC,
            key,
            message,
        )
    }
}