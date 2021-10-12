package com.galvao.wallet.service.impl;

import com.galvao.wallet.exception.BusinessException;
import com.galvao.wallet.grpc.service.dto.BalanceDto;
import com.galvao.wallet.infrastructure.entity.impl.AccountEntity;
import com.galvao.wallet.infrastructure.repository.AccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {
	private final AccountRepository accountRepository;

	@Autowired
	public AccountService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	public BalanceDto getBalance(Long userId) {
		AccountEntity account = getAccountEntity(userId);
		return new BalanceDto(userId, account.getGbpTotalAmount(), account.getEurTotalAmount(), account.getUsdTotalAmount());
	}

	protected AccountEntity getAccountEntity(Long userId) {
		Optional<AccountEntity> accountEntityOptional = accountRepository.findAccountEntityByUserId(userId);
		if (accountEntityOptional.isEmpty()) {
			throw new BusinessException(String.format("User %s not found.", userId));
		}
		return accountEntityOptional.get();
	}

	protected void saveAccountEntity(AccountEntity accountEntity) {
		accountRepository.save(accountEntity);
	}
}