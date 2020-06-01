package com.galvao.wallet.service.impl;

import com.galvao.wallet.enums.Currency;
import com.galvao.wallet.grpc.service.dto.TransactionDto;
import com.galvao.wallet.infrastructure.entity.impl.AccountEntity;
import com.galvao.wallet.infrastructure.entity.impl.WithdrawHistoricEntity;
import com.galvao.wallet.infrastructure.exception.InsufficientFoundException;
import com.galvao.wallet.infrastructure.repository.WithdrawHistoricRepository;
import com.galvao.wallet.service.TransactionI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import javax.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Qualifier("withdraw")
public class WithdrawService implements TransactionI {
	private AccountService accountService;
	private WithdrawHistoricRepository withdrawHistoricRepository;

	@Autowired
	public WithdrawService(AccountService accountService, WithdrawHistoricRepository withdrawHistoricRepository) {
		this.accountService = accountService;
		this.withdrawHistoricRepository = withdrawHistoricRepository;
	}

	@Override
	@Transactional
	public void process(TransactionDto transactionDto) {
		AccountEntity accountEntity = accountService.getAccountEntity(transactionDto.getUserId());
		withdrawAmount(transactionDto.getCurrency(), transactionDto.getAmount(), accountEntity);
		accountService.saveAccountEntity(accountEntity);
		WithdrawHistoricEntity historicEntity = new WithdrawHistoricEntity();
		historicEntity.setUserAccount(accountEntity.getUsers().stream()
				.filter(u -> u.getId().equals(transactionDto.getUserId())).findFirst().orElse(null));
		historicEntity.setAmount(transactionDto.getAmount());
		historicEntity.setCurrency(transactionDto.getCurrency());
		withdrawHistoricRepository.save(historicEntity);
	}

	private void withdrawAmount(Currency currency, BigDecimal withdrawAmount, AccountEntity accountEntity) {
		BigDecimal finalAmount;
		switch (currency) {
			case GBP:
				finalAmount = accountEntity.getGbpTotalAmount().subtract(withdrawAmount);
				checkWithdrawAmountAllowance(currency, finalAmount);
				accountEntity.setGbpTotalAmount(finalAmount);
				break;
			case EUR:
				finalAmount = accountEntity.getEurTotalAmount().subtract(withdrawAmount);
				checkWithdrawAmountAllowance(currency, finalAmount);
				accountEntity.setEurTotalAmount(finalAmount);
				break;
			case USD:
				finalAmount = accountEntity.getUsdTotalAmount().subtract(withdrawAmount);
				checkWithdrawAmountAllowance(currency, finalAmount);
				accountEntity.setUsdTotalAmount(finalAmount);
				break;
		}
	}

	private void checkWithdrawAmountAllowance(Currency currency, BigDecimal amountLeft) {
		if (amountLeft.compareTo(BigDecimal.ZERO) < 0) {
			log.info("Account has insufficient founds for the currency {}.", currency);
			throw new InsufficientFoundException();
		}
	}
}