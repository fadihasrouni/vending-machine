package com.fadihasrouni.vendingmachine.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.fadihasrouni.vendingmachine.controller.request.BuyRequest;
import com.fadihasrouni.vendingmachine.controller.request.UserPasswordRequest;
import com.fadihasrouni.vendingmachine.controller.request.UserRequest;
import com.fadihasrouni.vendingmachine.controller.response.BuyResponse;
import com.fadihasrouni.vendingmachine.controller.response.GenericResponse;
import com.fadihasrouni.vendingmachine.controller.response.ProductResponse;
import com.fadihasrouni.vendingmachine.controller.response.UserResponse;
import com.fadihasrouni.vendingmachine.exception.BadRequestException;
import com.fadihasrouni.vendingmachine.exception.ForbiddenException;
import com.fadihasrouni.vendingmachine.exception.ResourceNotFoundException;
import com.fadihasrouni.vendingmachine.exception.UniqueConstraintException;
import com.fadihasrouni.vendingmachine.model.User;
import com.fadihasrouni.vendingmachine.model.type.UserRole;
import com.fadihasrouni.vendingmachine.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ProductService productService;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	private Integer[] availableCoins = { 100, 50, 20, 10, 5 };

	/**
	 * Process and save user information
	 * 
	 * @param userRequest
	 * @return
	 */
	public UserResponse register(UserRequest userRequest) {
		// Make sure that password and confirmPassword match
		checkPasswordsMatch(userRequest.getPassword(), userRequest.getConfirmPassword());

		UserResponse userResponse = new UserResponse();
		
		// Get existing user if present
		User existingUser = userRepository.findByUsername(userRequest.getUsername()).orElse(null);

		// Check it user exists with same username (username must be unique)
		if (existingUser == null) {
			User newUser = populateUserModel(userRequest);
			newUser = userRepository.save(newUser);
			userResponse = populateUserResponse(newUser);
		} else {
			throw new UniqueConstraintException("User already exists with username=" + userRequest.getUsername());
		}

		return userResponse;
	}
	
	
	/**
	 * Get user by id or throw an exception
	 * 
	 * @param id
	 * @return
	 */
	public UserResponse findUserById(Long id) {
		// Validate if the user is allowed
		User existingUser = validateAndReturnUser(id, false);

		return populateUserResponse(existingUser);
	}
	
	/**
	 * Get user by username or throw an exception
	 * 
	 * @param id
	 * @return
	 */
	public UserResponse findUserByUsername(String username) {
		// Validate if the user is allowed
		User existingUser = validateAndReturnUserByUsername(username);

		return populateUserResponse(existingUser);
	}
	
	
	/**
	 * update already existing user
	 * 
	 * @param id
	 * @return
	 */
	public UserResponse updateUserPassword(UserPasswordRequest userPasswordRequest) {
		
		// Validate if the user is allowed
		User existingUser = validateAndReturnUser(userPasswordRequest.getId(), false);

		// Make sure that password and confirmPassword match
		checkPasswordsMatch(userPasswordRequest.getPassword(), userPasswordRequest.getConfirmPassword());

		existingUser.setPassword(bCryptPasswordEncoder.encode(userPasswordRequest.getPassword()));
		
		userRepository.save(existingUser);
		
		return populateUserResponse(existingUser);

	}
	
	/**
	 * Delete a specific user by id
	 * 
	 * @param id
	 * @return
	 */
	public GenericResponse deleteUser(Long id) {
		
		validateAndReturnUser(id, false);
		
		userRepository.deleteById(id);
		
		return  new GenericResponse(true, "User deleted successfully");
	}
	
	/**
	 * Deposit coin in the use deposit balance
	 * 
	 * @param id
	 * @param coin
	 * @return
	 */
	public GenericResponse deposit(Long id, Integer coin) {

		// Validate if the user is allowed
		User user = validateAndReturnUser(id, true);
		
		// Validate if the coin is supported
		if(!Arrays.stream(availableCoins).anyMatch(coin::equals)) {
			throw new BadRequestException("The coins must be in: " + availableCoins, null);
		}
		
		user.setDeposit(user.getDeposit() + coin);
		
		userRepository.save(user);

		return new GenericResponse(true, coin + " cent coin deposited successfully");
	}
	
	/**
	 * Buy a specific product
	 * 
	 * @param userId
	 * @param productId
	 * @return
	 */
	public BuyResponse buy(Long userId, BuyRequest request) {
		
		// Validate if the user is allowed
		User user = validateAndReturnUser(userId, true);
		
		// Get the product by id
		ProductResponse product = productService.findProductById(request.getProductId());
		
		Integer productCost = product.getCost() * request.getAmount();
		
		if(product.getAmountAvailable() < request.getAmount()) {
			throw new BadRequestException("The amount of product requested in not available", null);
		}
		
		if(user.getDeposit() < productCost) {
			throw new BadRequestException("You don't have enough balance in order to buy the product(s)", null);
		}
		
		Integer depositAfterBuying = user.getDeposit() - productCost;
				
		user.setDeposit(depositAfterBuying);
		
		// Save the new deposited amount
		userRepository.save(user);
		
		// Update product amount available
		product.setAmountAvailable(product.getAmountAvailable() - request.getAmount());
		productService.updateProductAmount(request.getProductId(),  product.getAmountAvailable());

		BuyResponse result = new BuyResponse();
		
		result.setMoneySpent(productCost);
		result.setProductBought(product);	
		result.setChange(getChange(depositAfterBuying));
		
		return result;
	}
	
	
	/**
	 * Reset the user deposit to 0
	 * 
	 * @param userId
	 * @return
	 */
	public GenericResponse resetDeposit(Long userId) {
		// Validate if the user is allowed
		User user = validateAndReturnUser(userId, true);
		
		user.setDeposit(0);
		
		userRepository.save(user);
		
		return new GenericResponse(true, "The deposit is now reset to 0.");
	}
	
	/**
	 * Returns the coins that are left in the user balance
	 * 
	 * @param depositAfterBuying
	 * @return
	 */
	private List<Integer> getChange(Integer depositAfterBuying){
		List<Integer> change = new ArrayList<>();

		for(int i = 0; i < availableCoins.length; i ++) {
			if (depositAfterBuying - availableCoins[i] >= 0) {
				depositAfterBuying = depositAfterBuying - availableCoins[i];
				change.add(availableCoins[i]);
			}
			
			if(depositAfterBuying == 0)
				break;

		}
		return change;
	}
	
	/**
	 * Check if the password and confirm password are a match
	 * 
	 * @param userRequest
	 */
	private void checkPasswordsMatch(String password, String confirmPassword) {
		if (!password.equals(confirmPassword)) {
			Map<String, String> errorMap = new HashMap<String, String>();
			errorMap.put("confirmPassword", "Password does not match with confirmed password");
			throw new BadRequestException("Paswords provided does not match!", errorMap);
		}
	}
	
	/**
	 * Validate if the user is allowed to get information using user id
	 * 
	 * @param id
	 */
	private User validateAndReturnUser(Long id, boolean onlyBuyer) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		UserResponse userResponse = (UserResponse) authentication.getPrincipal();
		
		if (userResponse == null) {
			throw new ForbiddenException("Operation not permitted! Couldn't find relevant user.");
		}
		
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No user exists with id: " + id));
		
		if(onlyBuyer && !userResponse.getUserRole().equals(UserRole.BUYER)) {
			throw new ForbiddenException("Operation not permitted! Only buyers can perform operation");
		}

		// Sellers are permitted to access/update all users information
		if (userResponse.getUserRole().equals(UserRole.SELLER)) {
			return user;
		}

		// User is not permitted to see other users information
		if (!userResponse.getId().equals(id)) {
			throw new ForbiddenException("Operation not permitted! You are not allowed to perform operations on other user data.");
		}
		
		return user;
	}
	
	
	/**
	 * Validate if the user is allowed to get information using useename
	 * 
	 * @param username
	 */
	private User validateAndReturnUserByUsername(String username) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		UserResponse userResponse = (UserResponse) authentication.getPrincipal();
		
		if (userResponse == null) {
			throw new ForbiddenException("Operation not permitted! Couldn't find relevant user.");
		}
		
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("No user exists with username: " + username));

		
		// Sellers are permitted to access/update all users information
		if (userResponse.getUserRole().equals(UserRole.SELLER)) {
			return user;
		}

		// User is not permitted to see other users information
		if (!userResponse.getUsername().equals(username)) {
			throw new ForbiddenException("Operation not permitted! You are not allowed to access data.");
		}
		
		return user;
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
		userResponse.setDeposit(user.getDeposit() != null ? user.getDeposit() : 0);
		userResponse.setUserRole(user.getUserRole());
		
		return userResponse;
	}

	/**
	 * Populates user model from the user request
	 * 
	 * @param userRequest
	 * @return
	 */
	private User populateUserModel(UserRequest userRequest) {
		User user = new User();
		user.setUsername(userRequest.getUsername());
		user.setPassword(bCryptPasswordEncoder.encode(userRequest.getPassword()));
		user.setDeposit(0);
		user.setUserRole(UserRole.valueOf(userRequest.getUserRole().toUpperCase()));

		return user;
	}
}
