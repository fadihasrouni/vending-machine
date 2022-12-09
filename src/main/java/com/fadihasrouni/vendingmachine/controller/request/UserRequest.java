package com.fadihasrouni.vendingmachine.controller.request;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class UserRequest {
	
	@NotBlank(message = "username is required")
	private String username;
	
	@NotBlank(message = "Password field is required")
	private String password;
	
	@Transient
	private String confirmPassword;
	
	private String userRole;

}
