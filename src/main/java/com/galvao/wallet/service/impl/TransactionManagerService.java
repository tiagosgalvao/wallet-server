package com.galvao.wallet.service.impl;

import com.galvao.wallet.exception.BusinessException;
import com.galvao.wallet.grpc.service.dto.TransactionDto;
import com.galvao.wallet.infrastructure.entity.impl.AccountEntity;
import com.galvao.wallet.infrastructure.kafka.message.TransactionKafkaDto;
import com.galvao.wallet.infrastructure.kafka.producer.AsyncProducerService;
import com.galvao.wallet.service.TransactionI;
import com.galvao.wallet.service.convert.MessageConverter;
import com.galvao.wallet.service.enums.TransactionType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import static com.galvao.wallet.service.convert.MessageConverter.createKafkaDtoFromDto;

@Slf4j
@Service
public class TransactionManagerService {
	private TransactionFactory transactionFactory;
	private AccountService accountService;
	private AsyncProducerService asyncProducerService;
	private KafkaService kafkaService;

	@Autowired
	public TransactionManagerService(TransactionFactory transactionFactory,
	                                 AccountService accountService,
	                                 AsyncProducerService asyncProducerService,
	                                 KafkaService kafkaService) {
		this.transactionFactory = transactionFactory;
		this.accountService = accountService;
		this.asyncProducerService = asyncProducerService;
		this.kafkaService = kafkaService;
	}

	public void start(TransactionType transactionType, TransactionDto transactionDto) {
		log.info("Start transaction type:{} data:{}", transactionType, transactionDto);
		AccountEntity accountEntity = accountService.getAccountEntity(transactionDto.getUserId());
		try {
			TransactionKafkaDto message = createKafkaDtoFromDto(transactionType, accountEntity.getId(), transactionDto);
			TransactionKafkaDto responseMessage = kafkaService.sendSynchronousMessage(message);
			process(transactionType, MessageConverter.createDtoFromKafkaDto(responseMessage));
		} catch (BusinessException ex) {
			throw ex;
		} catch (Exception ex) {
			asyncProducerService.sendAsyncMessage(createKafkaDtoFromDto(transactionType, accountEntity.getId(), transactionDto));
			throw ex;
		}
	}

	private void process(TransactionType transactionType, TransactionDto transactionDto) {
		TransactionI transactionI = transactionFactory.getTransactionService(transactionType);
		transactionI.process(transactionDto);
	}

}