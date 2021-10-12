package com.galvao.wallet.exception;

public final class UnknownCurrencyException extends RuntimeException {
	public UnknownCurrencyException() {
		super("unknown_currency");
	}
}
