package com.galvao.wallet.infrastructure.kafka.consumer;

import com.galvao.wallet.infrastructure.kafka.message.TransactionKafkaDto;
import com.galvao.wallet.service.audit.AuditLogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ConsumerService {
	private AuditLogService auditLogService;

	@Autowired
	public ConsumerService(AuditLogService auditLogService) {
		this.auditLogService = auditLogService;
	}

	@KafkaListener(topics = "${kafka.topic.audit.name}", groupId = "${kafka.topic.audit.consumer.group}")
	public void listenAudit(TransactionKafkaDto transactionKafkaDto) {
		log.info("Received Message {}: ", transactionKafkaDto);
		try {
			auditLogService.insertAuditLog(transactionKafkaDto);
		} catch (Exception ex) {
			log.error("Error recovering transaction data. Obj {} {}", transactionKafkaDto, ex);
			throw new RuntimeException("Error recovering transaction data.");
		}
	}
}