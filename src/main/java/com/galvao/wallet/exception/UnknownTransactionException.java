package com.galvao.wallet.exception;

public final class UnknownTransactionException extends RuntimeException {
	public UnknownTransactionException() {
		super("unknown_transaction");
	}
}
