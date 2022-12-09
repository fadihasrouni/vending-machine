package com.fadihasrouni.vendingmachine.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException  extends RuntimeException {
	private static final long serialVersionUID = 2L;

	public ForbiddenException(String message) {
		super(message);
	}
}
