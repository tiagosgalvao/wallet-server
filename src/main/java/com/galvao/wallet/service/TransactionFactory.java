package com.galvao.wallet.service;

import com.galvao.wallet.exception.UnknownTransactionException;
import com.galvao.wallet.grpc.Transaction;
import com.galvao.wallet.service.transaction.TransactionI;
import com.galvao.wallet.service.transaction.impl.DepositService;
import com.galvao.wallet.service.transaction.impl.WithdrawService;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionFactory {
	private final DepositService depositService;
	private final WithdrawService withdrawService;

	public TransactionI getTransactionService(Transaction.Type transactionType) {
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