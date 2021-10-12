package com.galvao.wallet.service.impl;

import com.galvao.wallet.infrastructure.kafka.message.TransactionKafkaDto;
import com.galvao.wallet.infrastructure.kafka.producer.ProducerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KafkaService {
	private final ProducerService producerService;

	@Autowired
	public KafkaService(ProducerService producerService) {
		this.producerService = producerService;
	}

	public TransactionKafkaDto sendSynchronousMessage(TransactionKafkaDto transactionKafkaDto) {
		log.info("Sending message to kafka: {}", transactionKafkaDto);
		return producerService.sendMessage(transactionKafkaDto);
	}
}