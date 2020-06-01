package com.galvao.wallet.grpc.service.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BalanceDto {
	Long userId;
	BigDecimal gbpAmount;
	BigDecimal eurAmount;
	BigDecimal usdAmount;
}
