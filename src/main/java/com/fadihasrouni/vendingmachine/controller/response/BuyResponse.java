package com.fadihasrouni.vendingmachine.controller.response;

import java.util.List;

import lombok.Data;

@Data
public class BuyResponse {
	private int moneySpent;
	private ProductResponse productBought;
	private List<Integer> change;
}
