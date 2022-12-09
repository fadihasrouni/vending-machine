package com.fadihasrouni.vendingmachine.controller.request;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class UserPasswordRequest {

	private Long id;

	@NotBlank(message = "Password field is required")
	private String password;

	@NotBlank(message = "Password field is required")
	private String confirmPassword;

}
