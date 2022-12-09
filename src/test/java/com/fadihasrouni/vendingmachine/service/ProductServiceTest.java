package com.fadihasrouni.vendingmachine.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fadihasrouni.vendingmachine.controller.request.ProductRequest;
import com.fadihasrouni.vendingmachine.controller.response.GenericResponse;
import com.fadihasrouni.vendingmachine.controller.response.ProductResponse;
import com.fadihasrouni.vendingmachine.controller.response.UserResponse;
import com.fadihasrouni.vendingmachine.exception.BadRequestException;
import com.fadihasrouni.vendingmachine.exception.ForbiddenException;
import com.fadihasrouni.vendingmachine.exception.ResourceNotFoundException;
import com.fadihasrouni.vendingmachine.model.Product;
import com.fadihasrouni.vendingmachine.model.User;
import com.fadihasrouni.vendingmachine.model.type.UserRole;
import com.fadihasrouni.vendingmachine.repository.ProductRespository;
import com.fadihasrouni.vendingmachine.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class ProductServiceTest {
	
	@InjectMocks
	private ProductService productService;
	
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private ProductRespository productRepository;
	
	
	private Long userId = 1L;
	private Integer userDeposit = 50;
	private String username = "fadi.hasrouni";
	private Long productId = 1L;
	private Integer productCost = 25;
	private Integer amountAvailable = 10;
	private String productName = "kinder";
	
	private Integer productUpdateCost = 55;
	private Integer amountAvailableUpdate = 15;
	private String productNameUpdate = "kinder 2";
	
	Product product = new Product();

	/**
	 * All tests are made with the assumption that the user is properly
	 * authenticated and is a SELLER 
	 * 
	 * TODO: This unit tests should cover all authentication test case
	 * scenarios (user not authenticated, not a seller...)
	 * 
	 */

	@BeforeEach
	void Setup(TestInfo info) {
		
		Authentication authentication = Mockito.mock(Authentication.class);
		UserResponse userResponse = new UserResponse();
		userResponse.setId(userId);
		userResponse.setUsername("fadi.hasrouni");
		userResponse.setDeposit(userDeposit);
		userResponse.setUserRole(UserRole.SELLER);

		if (!info.getDisplayName().equals("getProductSuccess()")
			&& !info.getDisplayName().equals("getProductFailureNotFound()")) {
			SecurityContext securityContext = Mockito.mock(SecurityContext.class);
			Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
			
			Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn(userResponse);
			
			SecurityContextHolder.setContext(securityContext);
		}

		product.setId(productId);
		product.setAmountAvailable(amountAvailable);
		product.setCost(productCost);
		product.setProductName(productName);
		product.setSeller(new User(userId, username, "", 0, UserRole.SELLER));
	}
	
		
	@Test
	void addNewProductSuccess() {
		
		mockUser();
		Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(product);
		
		ProductRequest productRequest = getProductRequest();
		
		ProductResponse productResponse = productService.addNewProduct(productRequest);
		
		assertEquals(productId, productResponse.getId());
		assertEquals(amountAvailable, productResponse.getAmountAvailable());
		assertEquals(productCost, productResponse.getCost());
		assertEquals(productName, productResponse.getProductName());
		assertEquals(username, productResponse.getSellerUsername());
	}
	
	@Test
	void addNewProductFailureCostNull() {
		productCost = null;
		ProductRequest productRequest = getProductRequest();
		
		assertThrows(BadRequestException.class, () -> {
			productService.addNewProduct(productRequest);
		});
	}
	
	@Test
	void addNewProductFailureCostNotDivisibleBy5() {
		productCost = 23;
		ProductRequest productRequest = getProductRequest();
		
		assertThrows(BadRequestException.class, () -> {
			productService.addNewProduct(productRequest);
		});
	}
	
	@Test
	void addNewProductFailureAmountNull() {
		amountAvailable = null;
		ProductRequest productRequest = getProductRequest();
		
		assertThrows(BadRequestException.class, () -> {
			productService.addNewProduct(productRequest);
		});
	}
	
	@Test
	void addNewProductFailureLowerThanZero() {
		amountAvailable = -1;
		ProductRequest productRequest = getProductRequest();
		
		assertThrows(BadRequestException.class, () -> {
			productService.addNewProduct(productRequest);
		});
	}
	
	@Test
	void updateProductSuccess() {
		mockUser();
		updateProducatForSave();
		Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(product);
		Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));

		ProductRequest productRequest = getProductUpdateRequest();

		ProductResponse productResponse = productService.updateProduct(productId, productRequest);
		
		assertEquals(productId, productResponse.getId());
		assertEquals(amountAvailableUpdate, productResponse.getAmountAvailable());
		assertEquals(productUpdateCost, productResponse.getCost());
		assertEquals(productNameUpdate, productResponse.getProductName());
		assertEquals(username, productResponse.getSellerUsername());
	}
	
	@Test
	void updateProductFailureProductNotFound() {
		ProductRequest productRequest = getProductUpdateRequest();

		assertThrows(ResourceNotFoundException.class, () -> {
			productService.updateProduct(productId, productRequest);
		});
	}
	
	@Test
	void updateProductOfAnotherSellerFailure() {
		
		User newSeller = new User();
		
		newSeller.setId(2L);
		newSeller.setUserRole(UserRole.SELLER);
		
		product.setSeller(newSeller);
		
		Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));
		
		ProductRequest productRequest = getProductUpdateRequest();

		assertThrows(ForbiddenException.class, () -> {
			productService.updateProduct(productId, productRequest);
		});
	}
	
	@Test
	void updateProductFailureCostNull() {
		productUpdateCost = null;
		Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));
		
		ProductRequest productRequest = getProductUpdateRequest();

		assertThrows(BadRequestException.class, () -> {
			productService.updateProduct(productId, productRequest);
		});
	}
	
	@Test
	void updateProductFailureProductCostNoDivisibleBy5() {
		productUpdateCost = 12;
		Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));
		
		ProductRequest productRequest = getProductUpdateRequest();

		assertThrows(BadRequestException.class, () -> {
			productService.updateProduct(productId, productRequest);
		});
	}
	
	@Test
	void updateProductFailureAmountNull() {
		amountAvailableUpdate = null;
		Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));
		
		ProductRequest productRequest = getProductUpdateRequest();

		assertThrows(BadRequestException.class, () -> {
			productService.updateProduct(productId, productRequest);
		});
	}
	
	@Test
	void updateProductFailureAmountLowerThanZero() {
		amountAvailableUpdate = null;
		Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));
		
		ProductRequest productRequest = getProductUpdateRequest();

		assertThrows(BadRequestException.class, () -> {
			productService.updateProduct(productId, productRequest);
		});
	}
	
	@Test
	void getProductSuccess() {
		Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));

		ProductResponse productResponse = productService.findProductById(productId);
		
		assertEquals(productId, productResponse.getId());
		assertEquals(amountAvailable, productResponse.getAmountAvailable());
		assertEquals(productCost, productResponse.getCost());
		assertEquals(productName, productResponse.getProductName());
		assertEquals(username, productResponse.getSellerUsername());
	}
	
	@Test
	void getProductFailureNotFound () {
		assertThrows(ResourceNotFoundException.class, () -> {
			productService.findProductById(productId);
		});
	}
		
	@Test
	void deleteProductSuccess() {
		Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));

		GenericResponse response = productService.deleteProduct(productId);
		
		assertEquals("Product was deleted successfully", response.getMessage());
		assertTrue(response.isSuccess());

	}
	
	
	@Test
	void deleteProductFailureProductNotFound() {
		assertThrows(ResourceNotFoundException.class, () -> {
			productService.deleteProduct(productId);
		});
	}
	
	@Test
	void deleteProductOfAnotherSellerFailure() {
		
		User newSeller = new User();
		
		newSeller.setId(2L);
		newSeller.setUserRole(UserRole.SELLER);
		
		product.setSeller(newSeller);
		
		Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));
		
		assertThrows(ForbiddenException.class, () -> {
			productService.deleteProduct(productId);
		});
	}
	
	private void updateProducatForSave() {
		product.setAmountAvailable(amountAvailableUpdate);
		product.setCost(productUpdateCost);
		product.setProductName(productNameUpdate);
		product.setSeller(new User(userId, username, "", 0, UserRole.SELLER));
	}
	
	private ProductRequest getProductRequest() {
		ProductRequest product = new ProductRequest();
		
		product.setAmountAvailable(amountAvailable);
		product.setCost(productCost);
		product.setProductName("Kinder");
		
		return product;
	}
	
	private ProductRequest getProductUpdateRequest() {
		ProductRequest product = new ProductRequest();
		
		product.setAmountAvailable(amountAvailableUpdate);
		product.setCost(productUpdateCost);
		product.setProductName(productNameUpdate);
		
		return product;
	}
	
	private void mockUser() {
		User user = new User();
		user.setId(userId);
		user.setUsername("fadi.hasrouni");
		user.setDeposit(50);
		user.setUserRole(UserRole.SELLER);
		
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));		
	}
}
