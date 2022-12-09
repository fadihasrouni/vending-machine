package com.fadihasrouni.vendingmachine.controller.request;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
	
	@NotBlank(message = "product name is required")
	private String productName;
	
	@NotBlank(message = "amount available is required")
	private Integer amountAvailable;
	
	@NotBlank(message = "product cost is required")
	private Integer cost;
}
