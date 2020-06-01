package com.galvao.wallet.service.audit;

import com.galvao.wallet.infrastructure.entity.audit.AuditLogEntity;
import com.galvao.wallet.infrastructure.kafka.message.TransactionKafkaDto;
import com.galvao.wallet.infrastructure.repository.audit.AuditLogRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuditLogService {
	private AuditLogRepository auditLogRepository;

	@Autowired
	public AuditLogService(AuditLogRepository auditLogRepository) {
		this.auditLogRepository = auditLogRepository;
	}

	public void insertAuditLog(TransactionKafkaDto transactionKafkaDto) {
		AuditLogEntity auditLogEntity = new AuditLogEntity();
		auditLogEntity.setAmount(transactionKafkaDto.getAmount());
		auditLogEntity.setCurrency(transactionKafkaDto.getCurrency());
		auditLogEntity.setUserId(transactionKafkaDto.getUserId());
		auditLogEntity.setTransactionType(transactionKafkaDto.getTransactionType());
		auditLogRepository.save(auditLogEntity);
	}

}