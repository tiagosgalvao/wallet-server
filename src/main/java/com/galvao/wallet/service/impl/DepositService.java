package com.galvao.wallet.service.impl;

import com.galvao.wallet.enums.Currency;
import com.galvao.wallet.grpc.service.dto.TransactionDto;
import com.galvao.wallet.infrastructure.entity.impl.AccountEntity;
import com.galvao.wallet.infrastructure.entity.impl.DepositHistoricEntity;
import com.galvao.wallet.infrastructure.repository.DepositHistoricRepository;
import com.galvao.wallet.service.TransactionI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import javax.transaction.Transactional;

@Service
@Qualifier("deposit")
public class DepositService implements TransactionI {
	private AccountService accountService;
	private DepositHistoricRepository depositHistoricRepository;

	@Autowired
	public DepositService(AccountService accountService, DepositHistoricRepository depositHistoricRepository) {
		this.accountService = accountService;
		this.depositHistoricRepository = depositHistoricRepository;
	}

	@Override
	@Transactional
	public void process(TransactionDto transactionDto) {
		AccountEntity accountEntity = accountService.getAccountEntity(transactionDto.getUserId());
		addAmount(transactionDto.getCurrency(), transactionDto.getAmount(), accountEntity);
		accountService.saveAccountEntity(accountEntity);
		DepositHistoricEntity historicEntity = new DepositHistoricEntity();
		historicEntity.setUserAccount(accountEntity.getUsers().stream()
				.filter(u -> transactionDto.getUserId().equals(u.getId())).findFirst().orElse(null));
		historicEntity.setAmount(transactionDto.getAmount());
		historicEntity.setCurrency(transactionDto.getCurrency());
		depositHistoricRepository.save(historicEntity);
	}

	private void addAmount(Currency currency, BigDecimal depositAmount, AccountEntity accountEntity) {
		switch (currency) {
			case GBP:
				accountEntity.setGbpTotalAmount(accountEntity.getGbpTotalAmount().add(depositAmount));
				break;
			case EUR:
				accountEntity.setEurTotalAmount(accountEntity.getEurTotalAmount().add(depositAmount));
				break;
			case USD:
				accountEntity.setUsdTotalAmount(accountEntity.getUsdTotalAmount().add(depositAmount));
				break;
		}
	}
}