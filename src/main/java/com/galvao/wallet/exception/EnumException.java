package com.galvao.wallet.exception;

public class EnumException extends BusinessException {
	public EnumException(String enumName) {
		super(String.format("Enum value not found:%s", enumName));
	}
}
