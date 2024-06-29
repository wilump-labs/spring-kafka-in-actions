package labs.wilump.kafka.practice.producer

import labs.wilump.kafka.practice.logger
import labs.wilump.kafka.practice.model.MyMessage
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.util.function.Supplier

@Component
class MyProducer : Supplier<Flux<Message<MyMessage>>> {
    private val log = logger()
    private val sinks : Sinks.Many<Message<MyMessage>> = Sinks.many().unicast().onBackpressureBuffer()

    init {
        log.info("MyProducer initialized")
    }

    fun sendMessage(msg: MyMessage) {
        val message: Message<MyMessage> = MessageBuilder
            .withPayload(msg)
            .setHeader(KafkaHeaders.KEY, "${msg.version}")
            .build()
        sinks.emitNext(message, Sinks.EmitFailureHandler.FAIL_FAST)
    }

    override fun get(): Flux<Message<MyMessage>> {
        return sinks.asFlux()
    }
}