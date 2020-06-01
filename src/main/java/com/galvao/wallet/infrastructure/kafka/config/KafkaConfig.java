package com.galvao.wallet.infrastructure.kafka.config;

import com.galvao.wallet.infrastructure.kafka.message.TransactionKafkaDto;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

	@Value("${kafka.url}")
	private String bootstrapServers;

	@Value("${kafka.topic.transaction.name}")
	private String topicName;

	@Value(value = "${kafka.topic.audit.name}")
	private String auditTopicName;

	@Value("${kafka.topic.transaction.consumer.group}")
	private String consumerGroup;

	@Bean
	public NewTopic topic() {
		return new NewTopic(topicName, 10, (short) 1);
	}

	@Bean
	public NewTopic auditTopic() {
		return new NewTopic(auditTopicName, 1, (short) 1);
	}

	@Bean
	public Map<String, Object> producerConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return props;
	}

	@Bean
	public ProducerFactory<String, TransactionKafkaDto> producerFactory() {
		return new DefaultKafkaProducerFactory<>(producerConfigs());
	}

	@Bean
	public KafkaTemplate<String, TransactionKafkaDto> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}

	@Bean
	public ReplyingKafkaTemplate<String, TransactionKafkaDto, TransactionKafkaDto> replyKafkaTemplate(ProducerFactory<String, TransactionKafkaDto> pf,
	                                                                                                  KafkaMessageListenerContainer<String, TransactionKafkaDto> container) {
		return new ReplyingKafkaTemplate<>(pf, container);
	}

	@Bean
	public KafkaMessageListenerContainer<String, TransactionKafkaDto> replyContainer(ConsumerFactory<String, TransactionKafkaDto> cf) {
		ContainerProperties containerProperties = new ContainerProperties(topicName);
		return new KafkaMessageListenerContainer<>(cf, containerProperties);
	}

	@Bean
	public Map<String, Object> consumerConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
		return props;
	}

	@Bean
	public ConsumerFactory<String, TransactionKafkaDto> consumerFactory() {
		return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(), new JsonDeserializer<>(TransactionKafkaDto.class));
	}

	@Bean
	public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, TransactionKafkaDto>> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, TransactionKafkaDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		factory.setReplyTemplate(kafkaTemplate());
		return factory;
	}

}