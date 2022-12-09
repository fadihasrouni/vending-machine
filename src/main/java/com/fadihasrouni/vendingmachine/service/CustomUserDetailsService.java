package com.fadihasrouni.vendingmachine.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.fadihasrouni.vendingmachine.controller.response.UserResponse;
import com.fadihasrouni.vendingmachine.exception.ResourceNotFoundException;
import com.fadihasrouni.vendingmachine.model.User;
import com.fadihasrouni.vendingmachine.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		return populateUserResponse(user);
	}

	/**
	 * load the user by id
	 * 
	 * @param id
	 * @return
	 */
	public UserResponse loadUserById(Long id) {
		User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		;
		return populateUserResponse(user);
	}

	/**
	 * Populates user response from user model
	 * 
	 * @param user
	 * @return
	 */
	private UserResponse populateUserResponse(User user) {
		UserResponse userResponse = new UserResponse();
		userResponse.setId(user.getId());
		userResponse.setUsername(user.getUsername());
		userResponse.setPassword(user.getPassword());
		userResponse.setUserRole(user.getUserRole());
		return userResponse;
	}
}
