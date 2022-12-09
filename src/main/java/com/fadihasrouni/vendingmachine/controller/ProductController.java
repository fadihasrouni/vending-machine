package com.fadihasrouni.vendingmachine.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fadihasrouni.vendingmachine.controller.request.ProductRequest;
import com.fadihasrouni.vendingmachine.controller.response.GenericResponse;
import com.fadihasrouni.vendingmachine.controller.response.ProductResponse;
import com.fadihasrouni.vendingmachine.service.ProductService;

@RestController
@CrossOrigin
@RequestMapping("/products")
public class ProductController {

	@Autowired
	private ProductService productService;

	@GetMapping("/{id}")
	public ProductResponse findProductById(@PathVariable Long id) {
		return productService.findProductById(id);
	}

	@GetMapping
	public List<ProductResponse> findAllProductsList() {
		return productService.findAllProductsList();
	}

	@PostMapping
	public ProductResponse addNewProduct(@RequestBody ProductRequest productRequest) {
		return productService.addNewProduct(productRequest);
	}

	@PutMapping("/{id}")
	public ProductResponse updateProduct(@PathVariable Long id, @RequestBody ProductRequest productRequest) {
		return productService.updateProduct(id, productRequest);
	}

	@DeleteMapping("/{id}")
	public GenericResponse deleteProduct(@PathVariable Long id) {
		return productService.deleteProduct(id);
	}
}
