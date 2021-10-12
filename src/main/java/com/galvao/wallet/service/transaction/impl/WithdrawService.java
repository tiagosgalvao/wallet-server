package com.galvao.wallet.service.transaction.impl;

import com.galvao.wallet.exception.BusinessException;
import com.galvao.wallet.exception.InsufficientFoundException;
import com.galvao.wallet.exception.UnknownCurrencyException;
import com.galvao.wallet.grpc.Currency;
import com.galvao.wallet.grpc.Transaction;
import com.galvao.wallet.infrastructure.entity.impl.AccountEntity;
import com.galvao.wallet.infrastructure.entity.impl.WithdrawHistoricEntity;
import com.galvao.wallet.infrastructure.repository.WithdrawHistoricRepository;
import com.galvao.wallet.service.AccountService;
import com.galvao.wallet.service.transaction.TransactionI;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Qualifier("withdraw")
@RequiredArgsConstructor
public class WithdrawService implements TransactionI {
	private final AccountService accountService;
	private final WithdrawHistoricRepository withdrawHistoricRepository;

	@Override
	@Transactional
	public void process(Transaction transaction) {
		try {
			AccountEntity accountEntity = accountService.getAccountEntity(transaction.getUserId());
			var updatedAccount = withdrawAmount(transaction.getCurrency(), BigDecimal.valueOf(transaction.getAmount()), accountEntity);
			accountService.saveAccountEntity(updatedAccount);
			WithdrawHistoricEntity historicEntity = new WithdrawHistoricEntity();
			historicEntity.setUserAccount(updatedAccount.getUsers().stream()
					.filter(u -> u.getId() == (transaction.getUserId())).findFirst()
					.orElseThrow(() -> new BusinessException("User not found.")));
			historicEntity.setAmount(BigDecimal.valueOf(transaction.getAmount()));
			historicEntity.setCurrency(transaction.getCurrency().name());
			withdrawHistoricRepository.save(historicEntity);
		} catch (Exception ex) {
			// TODO update transaction to FAILED status (create statuses)
			log.error(ex.getMessage());
		}
	}

	private AccountEntity withdrawAmount(Currency currency, BigDecimal withdrawAmount, AccountEntity accountEntity) {
		BigDecimal finalAmount;
		switch (currency) {
			case GBP:
				finalAmount = accountEntity.getGbpTotalAmount().subtract(withdrawAmount);
				checkWithdrawAmountAllowance(currency, finalAmount);
				accountEntity.setGbpTotalAmount(finalAmount);
				return accountEntity;
			case EUR:
				finalAmount = accountEntity.getEurTotalAmount().subtract(withdrawAmount);
				checkWithdrawAmountAllowance(currency, finalAmount);
				accountEntity.setEurTotalAmount(finalAmount);
				return accountEntity;
			case USD:
				finalAmount = accountEntity.getUsdTotalAmount().subtract(withdrawAmount);
				checkWithdrawAmountAllowance(currency, finalAmount);
				accountEntity.setUsdTotalAmount(finalAmount);
				return accountEntity;
			default:
				throw new UnknownCurrencyException(currency.name());
		}
	}

	private void checkWithdrawAmountAllowance(Currency currency, BigDecimal amountLeft) {
		if (amountLeft.compareTo(BigDecimal.ZERO) < 0) {
			log.info("Account has insufficient founds for the currency {}.", currency);
			throw new InsufficientFoundException();
		}
	}
}