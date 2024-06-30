package labs.wilump.kafka.practice.config.multiconfig

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.kafka.support.serializer.JsonDeserializer

@Profile("multi-config")
@EnableKafka
@Configuration
class KafkaJsonConfig {

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.kafka.json")
    fun kafkaProperties(): KafkaProperties {
        return KafkaProperties()
    }

    @Primary
    @Bean
    fun consumerFactory(kafkaProperties: KafkaProperties): ConsumerFactory<String, Any> {
        val props: MutableMap<String, Any> = mutableMapOf()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = kafkaProperties.consumer.keyDeserializer
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = kafkaProperties.consumer.valueDeserializer
        props[JsonDeserializer.TRUSTED_PACKAGES] = "*"
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "latest"
        props[ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG] = false
        return DefaultKafkaConsumerFactory(props)
    }

    @Primary
    @Bean
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<String, Any>,
    ): ConcurrentKafkaListenerContainerFactory<String, Any> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()
        factory.consumerFactory = consumerFactory
        factory.setConcurrency(1)
        return factory
    }

    @Primary
    @Bean
    fun producerFactory(kafkaProperties: KafkaProperties): ProducerFactory<String, Any> {
        val props: MutableMap<String, Any> = mutableMapOf()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = kafkaProperties.producer.keySerializer
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = kafkaProperties.producer.valueSerializer
        props[ProducerConfig.ACKS_CONFIG] = kafkaProperties.producer.acks
        return DefaultKafkaProducerFactory(props)
    }

    @Primary
    @Bean
    fun kafkaTemplate(
        kafkaProperties: KafkaProperties,
    ) : KafkaTemplate<String, Any> {
        return KafkaTemplate(producerFactory(kafkaProperties))
    }
}