package com.fadihasrouni.vendingmachine.controller.response;

import lombok.Data;

@Data
public class ProductResponse {
	private Long id;
	private String productName;
	private Integer amountAvailable;
	private Integer cost;
	private String sellerUsername;
}
