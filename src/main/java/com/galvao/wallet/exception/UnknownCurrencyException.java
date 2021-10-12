package com.galvao.wallet.exception;

public final class UnknownCurrencyException extends RuntimeException {
	public UnknownCurrencyException(String name) {
		super(String.format("unknown_currency: %s", name));
	}
}
