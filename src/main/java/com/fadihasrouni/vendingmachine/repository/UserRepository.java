package com.fadihasrouni.vendingmachine.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fadihasrouni.vendingmachine.model.User;

/**
 * UserRepository to handle user data db operations. 
 */
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);
}
