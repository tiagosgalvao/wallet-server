package com.galvao.wallet.service;

import com.galvao.wallet.exception.BusinessException;
import com.galvao.wallet.grpc.Transaction;
import com.galvao.wallet.grpc.TransactionRequest;
import com.galvao.wallet.grpc.service.dto.TransactionDto;
import com.galvao.wallet.infrastructure.entity.impl.AccountEntity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static org.springframework.kafka.support.KafkaHeaders.OFFSET;
import static org.springframework.kafka.support.KafkaHeaders.RECEIVED_TOPIC;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionManagerService {

	@Value("${kafka.topics.wallet}")
	private String topic;

	private final AccountService accountService;
	private final KafkaTemplate<String, Transaction> kafkaTemplate;
	private final TransactionFactory transactionFactory;

	public void start(Transaction.Type transactionType, TransactionRequest request) {
		validateGrpcRequest(request);
		AccountEntity accountEntity = accountService.getAccountEntity(request.getUserId());
		var transaction = Transaction.newBuilder()
				.setType(transactionType)
				.setAccountId(accountEntity.getId())
				.setUserId(request.getUserId())
				.setAmount(request.getAmount())
				.setCurrency(request.getCurrency()).build();
		log.info("Sending [{}] event to topic:{}", transaction, topic);
		kafkaTemplate.send(topic, String.valueOf(transaction.getAccountId()), transaction);
	}

	private void validateGrpcRequest(TransactionRequest transactionRequest) {
		var transactionDto = TransactionDto.builder()
				.userId(transactionRequest.getUserId())
				.amount(BigDecimal.valueOf(transactionRequest.getAmount()))
				.currency(transactionRequest.getCurrency().name())
				.build();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<TransactionDto>> violations = validator.validate(transactionDto);
		if (!CollectionUtils.isEmpty(violations)) {
			violations.forEach(v -> {
				throw new BusinessException(v.getMessage());
			});
		}
	}

	@RetryableTopic(attempts = "2", backoff = @Backoff(delay = 5000, multiplier = 2.0))
	@KafkaListener(
			topics = "${kafka.topics.wallet}",
			containerFactory = "transactionContainerFactory",
			groupId = "#{'${spring.application.name}' + '-transaction-process'}"
	)
	public void listener(Transaction transaction, @Header(RECEIVED_TOPIC) String topic, @Header(OFFSET) long offset) {
		log.info("EVENT IN -> event:[{}] from topic:{} @ offset:{}", transaction, topic, offset);
		try {
			var service = transactionFactory.getTransactionService(transaction.getType());
			service.process(transaction);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	@DltHandler
	public void dlt(Transaction transaction, @Header(RECEIVED_TOPIC) String topic, @Header(OFFSET) long offset) {
		log.info("DLT IN -> event:[{}] from topic:{} @ offset:{}", transaction, topic, offset);
	}
}