package com.galvao.wallet.infrastructure.kafka.producer;

import com.galvao.wallet.infrastructure.kafka.message.TransactionKafkaDto;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProducerService {
	private ReplyingKafkaTemplate<String, TransactionKafkaDto, TransactionKafkaDto> kafkaTemplate;
	@Value("${kafka.topic.transaction.name}")
	private String topicName;
	@Value(value = "${kafka.topic.audit.name}")
	private String auditTopicName;

	@Autowired
	public ProducerService(ReplyingKafkaTemplate<String, TransactionKafkaDto, TransactionKafkaDto> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public TransactionKafkaDto sendMessage(TransactionKafkaDto message) {
		// create producer record
		ProducerRecord<String, TransactionKafkaDto> record = new ProducerRecord<>(topicName, message.getAccountId().toString(), message);
		// set reply topic in header
		record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, topicName.getBytes()));
		// post in kafka topic
		RequestReplyFuture<String, TransactionKafkaDto, TransactionKafkaDto> sendAndReceive = kafkaTemplate.sendAndReceive(record);
		// confirm if producer produced successfully
		SendResult<String, TransactionKafkaDto> sendResult;
		try {
			sendResult = sendAndReceive.getSendFuture().get();
		} catch (Exception e) {
			log.error("KafkaException Error when expecting return in future - kafka {}", message, e);
			throw new RuntimeException("Error processing kafka message.");
		}
		//print all headers
		Objects.requireNonNull(sendResult).getProducerRecord().headers().forEach(header -> log.info("{} : {}", header.key(), header.value()));
		// get consumer record
		ConsumerRecord<String, TransactionKafkaDto> consumerRecord;
		try {
			consumerRecord = sendAndReceive.get();
		} catch (Exception e) {
			log.error("KafkaException Error when receiving return from future - kafka {}", message, e);
			throw new RuntimeException("Error processing kafka message.");
		}
		// return consumer value
		return consumerRecord.value();
	}

}