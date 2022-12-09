package com.fadihasrouni.vendingmachine.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenericResponse {
	private boolean success;
	private String message;
}
