package com.galvao.wallet.infrastructure.exception;

public final class UnknownTransactionException extends BusinessException {
	public UnknownTransactionException() {
		super("unknown_transaction");
	}
}
