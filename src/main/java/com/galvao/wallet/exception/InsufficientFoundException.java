package com.galvao.wallet.exception;

public final class InsufficientFoundException extends RuntimeException {
	public InsufficientFoundException() {
		super("insufficient_funds");
	}
}
