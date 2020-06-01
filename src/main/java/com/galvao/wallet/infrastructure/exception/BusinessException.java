package com.galvao.wallet.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = -2379402247642287122L;

	public BusinessException(String message) {
		super(message);
	}

}