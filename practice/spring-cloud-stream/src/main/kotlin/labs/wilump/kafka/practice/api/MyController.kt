package labs.wilump.kafka.practice.api

import labs.wilump.kafka.practice.model.MyMessage
import labs.wilump.kafka.practice.producer.MyProducer
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class MyController(
    private val producer: MyProducer
) {
    @PostMapping("/message")
    fun sendMessage(
        @RequestBody msg: MyMessage,
    ) {
        producer.sendMessage(msg)
    }
}