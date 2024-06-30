package labs.wilump.kafka.practice.api

import labs.wilump.kafka.practice.producer.MyStringProducer
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Profile("multi-config")
@RestController
class MyStringController(
    private val stringProducer: MyStringProducer,
) {
    @PostMapping("/string-message/{key}")
    fun sendStringMessage(
        @RequestBody msg: String,
        @PathVariable key: String,
    ) {
        stringProducer.sendMessageWithKey(key, msg)
    }
}