package com.galvao.wallet.grpc.service.dto;

import com.galvao.wallet.enums.Currency;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionDto {
	@NotNull(message = "User account is mandatory.")
	private Long userId;
	@NotNull(message = "Amount is mandatory.")
	@DecimalMin(value = "0.01", message = "Amount must be greater than zero.")
	private BigDecimal amount;
	@NotNull(message = "Currency is mandatory.")
	private Currency currency;
}