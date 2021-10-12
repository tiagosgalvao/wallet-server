package com.galvao.wallet.infrastructure.kafka.config;

import com.galvao.wallet.grpc.Transaction;
import com.galvao.wallet.infrastructure.kafka.filter.TransactionFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class KafkaListenerContainerFactories {
	private final ConsumerFactory<String, Transaction> consumerFactory;
	private final TransactionFilter transactionFilter;

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, Transaction> transactionContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, Transaction> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory);
		factory.setRecordFilterStrategy(transactionFilter);
		return factory;
	}
}
