package com.fadihasrouni.vendingmachine.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fadihasrouni.vendingmachine.model.Product;

/**
 * ProductRespository to handle user data db operations.
 */
public interface ProductRespository extends JpaRepository<Product, Long> {
	Optional<Product> findById(Long id);
}
