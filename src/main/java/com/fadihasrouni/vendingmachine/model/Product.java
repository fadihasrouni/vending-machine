package com.fadihasrouni.vendingmachine.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table
@Data
public class Product {
	
	@Id
	@GeneratedValue
	private Long id;

	private String productName;
	
	private Integer amountAvailable;
	
	private Integer cost;
	
	@ManyToOne(optional = true)
	private User seller;

}
