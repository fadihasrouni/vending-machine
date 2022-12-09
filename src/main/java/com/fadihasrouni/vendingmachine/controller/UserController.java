package com.fadihasrouni.vendingmachine.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fadihasrouni.vendingmachine.config.security.JwtTokenProvider;
import com.fadihasrouni.vendingmachine.controller.request.BuyRequest;
import com.fadihasrouni.vendingmachine.controller.request.LoginRequest;
import com.fadihasrouni.vendingmachine.controller.request.UserPasswordRequest;
import com.fadihasrouni.vendingmachine.controller.request.UserRequest;
import com.fadihasrouni.vendingmachine.controller.response.BuyResponse;
import com.fadihasrouni.vendingmachine.controller.response.GenericResponse;
import com.fadihasrouni.vendingmachine.controller.response.JWTLoginSuccessResponse;
import com.fadihasrouni.vendingmachine.controller.response.UserResponse;
import com.fadihasrouni.vendingmachine.service.UserService;
import com.fadihasrouni.vendingmachine.utils.Constants;


@RestController
@CrossOrigin
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenProvider tokenProvider;
		
	@PostMapping(path = "/register")
	public UserResponse registerUser(@RequestBody @Valid UserRequest userRequest) {
		return userService.register(userRequest);
	}
	
	@GetMapping("/{id}")
	public UserResponse findUserById(@PathVariable Long id) {
		return userService.findUserById(id);
	}
	
	@GetMapping("/username/{username}")
	public UserResponse findUserByUsername(@PathVariable String username) {
		return userService.findUserByUsername(username);
	}
	
	@PutMapping("/resetPassword")
	public UserResponse updateUser(@RequestBody @Valid UserPasswordRequest userPasswordRequest) {
		return userService.updateUserPassword(userPasswordRequest);
	}
		
	@DeleteMapping("/{id}")
	public GenericResponse deleteUser(@PathVariable Long id) {
		return userService.deleteUser(id);
	}
	
	@PostMapping("/login")
	public JWTLoginSuccessResponse authenticateUser(@RequestBody @Valid LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = Constants.TOKEN_PREFIX + tokenProvider.generateToken(authentication);

		return new JWTLoginSuccessResponse(true, jwt);
		
	}
	
	@PostMapping("/{id}/deposit/{coin}")
	public GenericResponse deposit(@PathVariable Long id, @PathVariable Integer coin) {
		return userService.deposit(id, coin);
	}
	
	@PostMapping("/{id}/buy")
	public BuyResponse buy(@PathVariable Long id, @RequestBody BuyRequest request) {
		return userService.buy(id, request);
	}
	
	@PostMapping("/{id}/reset")
	public GenericResponse resetBalance(@PathVariable Long id) {
		return userService.resetDeposit(id);
	}
	
}
