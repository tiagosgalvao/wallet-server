package com.galvao.wallet.infrastructure.kafka.message;

import com.galvao.wallet.enums.Currency;
import com.galvao.wallet.service.enums.TransactionType;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionKafkaDto {
	@NotNull
	private Long accountId;
	@NotNull
	private Long userId;
	@NotNull
	private BigDecimal amount;
	@NotNull
	private Currency currency;
	@NotNull
	private TransactionType transactionType;
}