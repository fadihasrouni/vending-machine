package com.fadihasrouni.vendingmachine.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fadihasrouni.vendingmachine.controller.request.BuyRequest;
import com.fadihasrouni.vendingmachine.controller.response.BuyResponse;
import com.fadihasrouni.vendingmachine.controller.response.GenericResponse;
import com.fadihasrouni.vendingmachine.controller.response.ProductResponse;
import com.fadihasrouni.vendingmachine.controller.response.UserResponse;
import com.fadihasrouni.vendingmachine.exception.BadRequestException;
import com.fadihasrouni.vendingmachine.model.User;
import com.fadihasrouni.vendingmachine.model.type.UserRole;
import com.fadihasrouni.vendingmachine.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ProductService productService;

	private Long userId = 1L;
	private int userDeposit = 50;
	private Long productId = 1L;
	private int productCost = 25;

	/**
	 * All tests are made with the assumption that the user is properly
	 * authenticated and is a buyer 
	 * 
	 * TODO: This unit tests should cover all test case
	 * scenario (user not authenticated, not a buyer...)
	 * 
	 */

	@BeforeEach
	void setup() {
		Authentication authentication = Mockito.mock(Authentication.class);
		UserResponse userResponse = new UserResponse();
		userResponse.setId(userId);
		userResponse.setUsername("fadi.hasrouni");
		userResponse.setDeposit(userDeposit);
		userResponse.setUserRole(UserRole.BUYER);

		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn(userResponse);

		SecurityContextHolder.setContext(securityContext);

		User user = new User();
		user.setId(userId);
		user.setUsername("fadi.hasrouni");
		user.setDeposit(50);
		user.setUserRole(UserRole.BUYER);

		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
	}

	@Test
	void depositSuccess() {
		int centsToDeposit = 10;
		GenericResponse response = userService.deposit(userId, centsToDeposit);

		assertEquals(true, response.isSuccess());
		assertEquals(centsToDeposit + " cent coin deposited successfully", response.getMessage());
	}

	@Test
	void depositWrongCoin() {
		int centsToDeposit = 12;
		assertThrows(BadRequestException.class, () -> {
			userService.deposit(userId, centsToDeposit);
		});
	}

	@Test
	void buySuccess() {

		ProductResponse product = mockProduct(10);

		BuyRequest buyRequest = new BuyRequest();

		buyRequest.setProductId(productId);
		buyRequest.setAmount(1);

		BuyResponse response = userService.buy(userId, buyRequest);

		Integer[] change = { 20, 5};

		assertEquals(productCost, response.getMoneySpent());
		assertEquals(product, response.getProductBought());
		assertArrayEquals(change, response.getChange().toArray());
	}

	@Test
	void buyNotEnoughProduct() {

		mockProduct(1);

		BuyRequest buyRequest = new BuyRequest();

		buyRequest.setProductId(productId);
		buyRequest.setAmount(2);

		assertThrows(BadRequestException.class, () -> {
			userService.buy(userId, buyRequest);
		});

	}
	
	
	@Test
	void buyNotEnoughBalance() {
		
		productCost = 100;

		mockProduct(10);

		BuyRequest buyRequest = new BuyRequest();

		buyRequest.setProductId(productId);
		buyRequest.setAmount(2);

		assertThrows(BadRequestException.class, () -> {
			userService.buy(userId, buyRequest);
		});

	}

	private ProductResponse mockProduct(Integer amountAvailble) {
		ProductResponse product = new ProductResponse();

		product.setId(productId);
		product.setAmountAvailable(amountAvailble);
		product.setCost(productCost);
		product.setProductName("Kinder");

		Mockito.when(productService.findProductById(productId)).thenReturn(product);

		return product;
	}
}
