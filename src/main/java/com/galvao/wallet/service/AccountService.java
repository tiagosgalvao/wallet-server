package com.galvao.wallet.service;

import com.galvao.wallet.exception.BusinessException;
import com.galvao.wallet.grpc.BalanceResponse;
import com.galvao.wallet.infrastructure.entity.impl.AccountEntity;
import com.galvao.wallet.infrastructure.repository.AccountRepository;

import org.springframework.stereotype.Service;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

	private final AccountRepository accountRepository;

	public BalanceResponse getBalance(Long userId) {
		AccountEntity account = getAccountEntity(userId);
		return BalanceResponse.newBuilder()
				.setUserId(userId)
				.setGbpAmount(account.getGbpTotalAmount().doubleValue()).
				setEurAmount(account.getEurTotalAmount().doubleValue())
				.setUsdAmount(account.getUsdTotalAmount().doubleValue())
				.build();
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