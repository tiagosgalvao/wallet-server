package com.galvao.wallet.service.convert;

import com.galvao.wallet.enums.Currency;
import com.galvao.wallet.grpc.TransactionRequest;
import com.galvao.wallet.grpc.service.dto.TransactionDto;
import com.galvao.wallet.infrastructure.exception.UnknownCurrencyException;
import com.galvao.wallet.infrastructure.kafka.message.TransactionKafkaDto;
import com.galvao.wallet.service.enums.TransactionType;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MessageConverter {

	private MessageConverter() {
	}

	public static TransactionKafkaDto createKafkaDtoFromDto(TransactionType transactionType, Long accountId, TransactionDto transactionDto) {
		return new TransactionKafkaDto(accountId,
				transactionDto.getUserId(),
				transactionDto.getAmount(),
				transactionDto.getCurrency(),
				transactionType);
	}

	public static TransactionDto createDtoFromKafkaDto(TransactionKafkaDto transactionKafkaDto) {
		return new TransactionDto(transactionKafkaDto.getUserId(),
				transactionKafkaDto.getAmount(),
				transactionKafkaDto.getCurrency());
	}

	public static TransactionDto createDtoFromGrpc(TransactionRequest transactionRequest) {
		try {
			Currency currency = Enum.valueOf(Currency.class, transactionRequest.getCurrency().toString());
			return new TransactionDto(transactionRequest.getUserId(), BigDecimal.valueOf(transactionRequest.getAmount()), currency);
		} catch (IllegalArgumentException e) {
			throw new UnknownCurrencyException();
		}
	}
}