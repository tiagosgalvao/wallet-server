package com.galvao.wallet.service.impl;

import com.galvao.wallet.infrastructure.exception.UnknownTransactionException;
import com.galvao.wallet.service.TransactionI;
import com.galvao.wallet.service.enums.TransactionType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionFactory {
	private DepositService depositService;
	private WithdrawService withdrawService;

	@Autowired
	public TransactionFactory(DepositService depositService, WithdrawService withdrawService) {
		this.depositService = depositService;
		this.withdrawService = withdrawService;
	}

	public TransactionI getTransactionService(TransactionType transactionType) {
		switch (transactionType) {
			case DEPOSIT:
				return depositService;
			case WITHDRAW:
				return withdrawService;
			default:
				throw new UnknownTransactionException();
		}
	}
}