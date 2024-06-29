package labs.wilump.kafka.practice.consumer

import labs.wilump.kafka.practice.model.MyMessage
import labs.wilump.kafka.practice.logger
import org.springframework.context.annotation.Bean
import org.springframework.messaging.Message
import org.springframework.stereotype.Component
import java.util.function.Consumer

@Component
class MyConsumerConfig {
    private val log = logger()

    init {
        log.info("MyConsumer initialized")
    }

    @Bean
    fun myConsumer() = Consumer<Message<MyMessage>> {
        log.info("Received message: ${it.payload}")
    }
}