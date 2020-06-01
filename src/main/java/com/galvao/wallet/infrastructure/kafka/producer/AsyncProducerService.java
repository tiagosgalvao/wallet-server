package com.galvao.wallet.infrastructure.kafka.producer;

import com.galvao.wallet.infrastructure.kafka.message.TransactionKafkaDto;
import com.galvao.wallet.service.audit.AuditLogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AsyncProducerService {
	private ReplyingKafkaTemplate<String, TransactionKafkaDto, TransactionKafkaDto> kafkaTemplate;
	private AuditLogService auditLogService;
	@Value("${kafka.topic.transaction.name}")
	private String topicName;
	@Value(value = "${kafka.topic.audit.name}")
	private String auditTopicName;

	@Autowired
	public AsyncProducerService(ReplyingKafkaTemplate kafkaTemplate, AuditLogService auditLogService) {
		this.kafkaTemplate = kafkaTemplate;
		this.auditLogService = auditLogService;
	}

	public void sendAsyncMessage(TransactionKafkaDto message) {
		log.info("Start sending message [{}]", message);

		ListenableFuture<SendResult<String, TransactionKafkaDto>> future = kafkaTemplate.send(auditTopicName, message);
		future.addCallback(new ListenableFutureCallback<>() {
			@Override
			public void onFailure(Throwable ex) {
				log.error("MESSAGE NOT SENT = [{}]", message, ex);
				auditLogService.insertAuditLog(message);
			}

			@Override
			public void onSuccess(SendResult<String, TransactionKafkaDto> result) {
				log.info("MESSAGE SENT = [{}]", message);
			}
		});
	}

}