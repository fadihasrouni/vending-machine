package com.fadihasrouni.vendingmachine.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UniqueConstraintException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UniqueConstraintException(String message) {
		super(message);
	}
}
