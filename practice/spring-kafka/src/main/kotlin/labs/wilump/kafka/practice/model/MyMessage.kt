package labs.wilump.kafka.practice.model

data class MyMessage(
    var id: String = "",
    var version: Int = 0,
    var content: String = "",
)
