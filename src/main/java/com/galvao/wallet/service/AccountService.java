package com.galvao.wallet.service;

import com.galvao.wallet.exception.BusinessException;
import com.galvao.wallet.grpc.service.dto.BalanceDto;
import com.galvao.wallet.infrastructure.entity.impl.AccountEntity;
import com.galvao.wallet.infrastructure.repository.AccountRepository;

import org.springframework.stereotype.Service;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

	private final AccountRepository accountRepository;

	public BalanceDto getBalance(Long userId) {
		AccountEntity account = getAccountEntity(userId);
		return new BalanceDto(userId, account.getGbpTotalAmount(), account.getEurTotalAmount(), account.getUsdTotalAmount());
	}

	public AccountEntity getAccountEntity(Long userId) {
		Optional<AccountEntity> accountEntityOptional = accountRepository.findAccountEntityByUserId(userId);
		if (accountEntityOptional.isEmpty()) {
			throw new BusinessException(String.format("User %s not found.", userId));
		}
		return accountEntityOptional.get();
	}

	public void saveAccountEntity(AccountEntity accountEntity) {
		accountRepository.save(accountEntity);
	}
}