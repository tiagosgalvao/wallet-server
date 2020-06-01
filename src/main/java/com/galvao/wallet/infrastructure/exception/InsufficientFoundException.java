package com.galvao.wallet.infrastructure.exception;

public final class InsufficientFoundException extends BusinessException {
	public InsufficientFoundException() {
		super("insufficient_funds");
	}
}
