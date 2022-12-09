package com.fadihasrouni.vendingmachine.model.type;

public enum UserRole {
	BUYER("BUYER"), SELLER("SELLER");

	private final String name;

	private UserRole(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
