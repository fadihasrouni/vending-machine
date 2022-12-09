package com.fadihasrouni.vendingmachine.exception.error;

import lombok.Data;

@Data
public class InvalidLoginResponse {
	private String username;
	private String password;
	private String message;

	public InvalidLoginResponse() {
		this.username = "Invalid Username";
		this.password = "Invalid Password";
		this.message = "Wrong username/password or incorrect token provided.";
	}
}