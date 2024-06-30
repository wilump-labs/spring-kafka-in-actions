package labs.wilump.kafka.practice.config.multiconfig

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.kafka.support.serializer.JsonDeserializer

@Profile("multi-config")
@EnableKafka
@Configuration
class KafkaStringConfig {

    @Qualifier("stringKafkaProperties")
    @Bean
    @ConfigurationProperties(prefix = "spring.kafka.string")
    fun stringKafkaProperties(): KafkaProperties {
        return KafkaProperties()
    }

    @Qualifier("stringConsumerFactory")
    @Bean
    fun stringConsumerFactory(stringKafkaProperties: KafkaProperties): ConsumerFactory<String, Any> {
        val props: MutableMap<String, Any> = mutableMapOf()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = stringKafkaProperties.bootstrapServers
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = stringKafkaProperties.consumer.keyDeserializer
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = stringKafkaProperties.consumer.valueDeserializer
        props[JsonDeserializer.TRUSTED_PACKAGES] = "*"
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "latest"
        props[ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG] = false
        return DefaultKafkaConsumerFactory(props)
    }

    @Qualifier("stringKafkaListenerContainerFactory")
    @Bean
    fun stringKafkaListenerContainerFactory(
        stringConsumerFactory: ConsumerFactory<String, Any>,
    ): ConcurrentKafkaListenerContainerFactory<String, Any> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()
        factory.consumerFactory = stringConsumerFactory
        factory.setConcurrency(1)
        return factory
    }

    @Qualifier("stringProducerFactory")
    @Bean
    fun stringProducerFactory(stringKafkaProperties: KafkaProperties): ProducerFactory<String, String> {
        val props: MutableMap<String, Any> = mutableMapOf()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = stringKafkaProperties.bootstrapServers
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = stringKafkaProperties.producer.keySerializer
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = stringKafkaProperties.producer.valueSerializer
        props[ProducerConfig.ACKS_CONFIG] = stringKafkaProperties.producer.acks
        return DefaultKafkaProducerFactory(props)
    }

    @Qualifier("stringKafkaTemplate")
    @Bean
    fun stringKafkaTemplate(
        stringKafkaProperties: KafkaProperties,
    ) : KafkaTemplate<String, String> {
        return KafkaTemplate(stringProducerFactory(stringKafkaProperties))
    }
}