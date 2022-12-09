package com.fadihasrouni.vendingmachine.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JWTLoginSuccessResponse {
	private boolean success;
	private String token;
}
