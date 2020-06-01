package com.galvao.wallet.infrastructure.exception;

public final class UnknownCurrencyException extends BusinessException {
	public UnknownCurrencyException() {
		super("unknown_currency");
	}
}
