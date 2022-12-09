package com.fadihasrouni.vendingmachine.controller.request;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class LoginRequest {
	@NotBlank(message = "username is required")
	private String username;
	@NotBlank(message = "Password field is required")
	private String password;
}
