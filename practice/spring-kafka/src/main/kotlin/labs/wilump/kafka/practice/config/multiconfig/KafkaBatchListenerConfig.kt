package labs.wilump.kafka.practice.config.multiconfig

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.support.serializer.JsonDeserializer

@Profile("multi-config")
@EnableKafka
@Configuration
class KafkaBatchListenerConfig {

    @Bean
    fun batchConsumerFactory(kafkaProperties: KafkaProperties): ConsumerFactory<String, Any> {
        val props: MutableMap<String, Any> = mutableMapOf()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = kafkaProperties.consumer.keyDeserializer
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = kafkaProperties.consumer.valueDeserializer
        props[JsonDeserializer.TRUSTED_PACKAGES] = "*"
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "latest"
        props[ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG] = false
        props[ConsumerConfig.MAX_POLL_RECORDS_CONFIG] = ConsumerConfig.DEFAULT_MAX_POLL_RECORDS
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun batchKafkaListenerContainerFactory(
        batchConsumerFactory: ConsumerFactory<String, Any>,
    ): ConcurrentKafkaListenerContainerFactory<String, Any> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()
        factory.consumerFactory = batchConsumerFactory
        factory.setConcurrency(1)

        // Batch listener 설정
        factory.isBatchListener = true
        factory.containerProperties.ackMode = ContainerProperties.AckMode.BATCH

        return factory
    }
}