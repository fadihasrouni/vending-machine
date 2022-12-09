package com.fadihasrouni.vendingmachine.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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

@Service
public class ProductService {
	
	@Autowired
	private ProductRespository productRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	
	/**
	 * Find the product by a specific product id
	 * 
	 * @param id
	 * @return
	 */
	public ProductResponse findProductById(Long id) {
		
		Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Could not find product with id: " + id));
		return populateProductResponse(product);
	}
	
	/**
	 * Find all products available
	 * 
	 * @param id
	 * @return
	 */
	public List<ProductResponse> findAllProductsList() {
		List<Product> products = productRepository.findAll();
		return populateProductListResponse(products);
	}
	
	/**
	 * Add a new product
	 * 
	 * @param productRequest
	 * @return
	 */
	public ProductResponse addNewProduct(ProductRequest productRequest) {
		
		UserResponse seller = getSellerInformation();
		
		validateCost(productRequest.getCost());
		validateAmount(productRequest.getAmountAvailable());
		
		User user = userRepository.findById(seller.getId()).orElseThrow(() -> new ResourceNotFoundException("Could not find user"));
		
		Product product = populateProductModel(productRequest, user);
		
		product = productRepository.save(product);
		
		return populateProductResponse(product);
	}
	
	/**
	 * Update a given product
	 * 
	 * @param id
	 * @param productRequest
	 * @return
	 */
	public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
				
		UserResponse seller = getSellerInformation();
		
		Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Couldn't find product with id: " + id));
		
		if(!product.getSeller().getId().equals(seller.getId())) {
			throw new ForbiddenException("Couldn't update another seller product");
		}
		
		validateCost(productRequest.getCost());
		validateAmount(productRequest.getAmountAvailable());
		
		User user = userRepository.findById(seller.getId()).orElseThrow(() -> new ResourceNotFoundException("Could not find user"));

		Product updatedProduct = populateProductModel(productRequest, user);
		
		updatedProduct.setId(id);
		
		updatedProduct = productRepository.save(updatedProduct);
		
		return populateProductResponse(updatedProduct);
	}
	
	/**
	 * Update a given product available stock amount
	 * 
	 * @param id
	 * @param productRequest
	 * @return
	 */
	public ProductResponse updateProductAmount(Long id, Integer amount) {
						
		Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Couldn't find product with id: " + id));
		
		validateAmount(amount);

		product.setAmountAvailable(amount);

		product = productRepository.save(product);
		
		return populateProductResponse(product);
	}
	
	/**
	 * Delete a given product by id
	 * 
	 * @param id
	 * @return
	 */
	public GenericResponse deleteProduct(Long id) {
		UserResponse seller = getSellerInformation();

		Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Couldn't find product with id: " + id));

		if(!product.getSeller().getId().equals(seller.getId())) {
			throw new ForbiddenException("Couldn't delete another seller product");
		}
		
		productRepository.delete(product);
				
		return new GenericResponse(true, "Product was deleted successfully");
	}
	
	/**
	 * Validate if the cost is valid
	 * 
	 * @param cost
	 */
	private void validateCost(Integer cost) {
		if(cost == null) {
			throw new BadRequestException("Cost cannot be null", null);
		}
		
		if(cost % 5 != 0) {
			throw new BadRequestException("Cost should be divisible by 5", null);
		}
	}
	
	/**
	 * Validate if the amount is correct
	 * 
	 * @param cost
	 */
	private void validateAmount(Integer amount) {
		if(amount == null) {
			throw new BadRequestException("Amount available cannot be null", null);
		}
		
		if(amount < 0) {
			throw new BadRequestException("Amount available should be larger than 0", null);
		}
	}
	
	/**
	 * Get the authenticated seller information from spring context 
	 * 
	 * @return
	 */
	private UserResponse getSellerInformation() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		UserResponse userResponse = (UserResponse) authentication.getPrincipal();
		
		if (userResponse == null) {
			throw new ForbiddenException("Operation not permitted! Couldn't find relevant user.");
		}
		
		// Checking if the user is a seller (2nd layer of security)
		if (!userResponse.getUserRole().equals(UserRole.SELLER)) {
			throw new ForbiddenException("Operation not permitted! You are not allowed to access data.");
		}

		return userResponse;
	}
	
	/**
	 * Prepare product response from product model
	 * 
	 * @param products
	 */
	private List<ProductResponse> populateProductListResponse(List<Product> products) {
		List<ProductResponse> productResponseList = new ArrayList<>();
		
		
		for (Product product : products) {
			productResponseList.add(populateProductResponse(product));
		}
		
		return productResponseList;
	}
	
	
	/**
	 * Prepare product response from product model
	 * 
	 * @param product
	 */
	private ProductResponse populateProductResponse(Product product) {
		ProductResponse response = new ProductResponse();
		
		response.setId(product.getId());
		response.setProductName(product.getProductName());
		response.setAmountAvailable(product.getAmountAvailable());
		response.setCost(product.getCost());
		response.setSellerUsername(product.getSeller().getUsername());
		
		return response;
	}
	
	/**
	 * Prepare product model from product request
	 * 
	 * @param product
	 */
	private Product populateProductModel(ProductRequest productRequest, User seller) {
		Product productModel = new Product();
		
		productModel.setProductName(productRequest.getProductName());
		productModel.setAmountAvailable(productRequest.getAmountAvailable());
		productModel.setCost(productRequest.getCost());
		productModel.setSeller(seller);
		
		return productModel;
	}

}
