package com.fadihasrouni.vendingmachine.exception.error;

import java.util.Date;
import java.util.Map;

public class ErrorDetails {
	private Date timestamp;
	private String message;
	private Map<String, String> errorMap;
	private String details;

	public ErrorDetails(Date timestamp, String message, String details) {
		super();
		this.timestamp = timestamp;
		this.message = message;
		this.details = details;
	}

	public ErrorDetails(Date timestamp, String message, String details, Map<String, String> errorMap) {
		super();
		this.timestamp = timestamp;
		this.message = message;
		this.details = details;
		this.errorMap = errorMap;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Map<String, String> getErrorMap() {
		return errorMap;
	}

	public void setErrorMap(Map<String, String> errorMap) {
		this.errorMap = errorMap;
	}
}
