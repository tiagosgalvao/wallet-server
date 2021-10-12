package com.galvao.wallet.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@ResponseStatus(UNPROCESSABLE_ENTITY)
public class BusinessException extends RuntimeException {
	public BusinessException(String message) {
		super(message);
	}
}