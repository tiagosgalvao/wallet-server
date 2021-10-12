package com.galvao.wallet.service.transaction.impl;

import com.galvao.wallet.exception.BusinessException;
import com.galvao.wallet.exception.UnknownCurrencyException;
import com.galvao.wallet.grpc.Currency;
import com.galvao.wallet.grpc.Transaction;
import com.galvao.wallet.infrastructure.entity.impl.AccountEntity;
import com.galvao.wallet.infrastructure.entity.impl.DepositHistoricEntity;
import com.galvao.wallet.infrastructure.repository.DepositHistoricRepository;
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
@Qualifier("deposit")
@RequiredArgsConstructor
public class DepositService implements TransactionI {
	private final AccountService accountService;
	private final DepositHistoricRepository depositHistoricRepository;

	@Override
	@Transactional
	public void process(Transaction transaction) {
		try {
			AccountEntity accountEntity = accountService.getAccountEntity(transaction.getUserId());
			var updatedAccount = addAmount(transaction.getCurrency(), BigDecimal.valueOf(transaction.getAmount()), accountEntity);
			accountService.saveAccountEntity(updatedAccount);
			DepositHistoricEntity historicEntity = new DepositHistoricEntity();
			historicEntity.setUserAccount(updatedAccount.getUsers().stream()
					.filter(u -> transaction.getUserId() == (u.getId())).findFirst()
					.orElseThrow(() -> new BusinessException("User not found.")));
			historicEntity.setAmount(BigDecimal.valueOf(transaction.getAmount()));
			historicEntity.setCurrency(transaction.getCurrency().name());
			depositHistoricRepository.save(historicEntity);
		} catch (Exception ex) {
			// TODO update transaction to FAILED status (create statuses)
			log.error(ex.getMessage());
		}
	}

	private AccountEntity addAmount(Currency currency, BigDecimal depositAmount, AccountEntity accountEntity) {
		switch (currency) {
			case GBP:
				accountEntity.setGbpTotalAmount(accountEntity.getGbpTotalAmount().add(depositAmount));
				return accountEntity;
			case EUR:
				accountEntity.setEurTotalAmount(accountEntity.getEurTotalAmount().add(depositAmount));
				return accountEntity;
			case USD:
				accountEntity.setUsdTotalAmount(accountEntity.getUsdTotalAmount().add(depositAmount));
				return accountEntity;
			default:
				throw new UnknownCurrencyException(currency.name());
		}
	}
}