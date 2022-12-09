package com.fadihasrouni.vendingmachine.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	@Configuration
	public class SpringFoxConfig {
		@Bean
		public Docket api() {
			return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
					.apis(RequestHandlerSelectors.basePackage("com.fadihasrouni.vendingmachine.controller"))
					.paths(PathSelectors.any()).build();
		}

		private ApiInfo apiInfo() {
			return new ApiInfo("Vending machine REST API",
					"This API consits of web services for vending machine operations",
					"API TOS", "Terms of service", new Contact("Fadi Hasrouni", "", "fadi.hasrouni@gmail.com"),
					"License of API", "--", Collections.emptyList());
		}
	}
}

