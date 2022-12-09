package com.fadihasrouni.vendingmachine.controller.request;

import lombok.Data;

@Data
public class BuyRequest {
	private Long productId;
	private Integer amount;
}
