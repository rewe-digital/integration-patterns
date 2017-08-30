package com.rewedigital.examples.msintegration.productdetailpage.product;

import static java.lang.String.format;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ProductWebController {

	private final ProductRepository products;

	@Autowired
	public ProductWebController(final ProductRepository products) {
		this.products = products;
	}

	@GetMapping("/products/{productId}")
	public ModelAndView productDetails(@PathVariable("productId") final String productId) {
		return getProductById(productId).map(p -> {
			final ModelAndView result = new ModelAndView("product");
			result.addObject("product", p);
			return result;
		}).orElseThrow(() -> productNotFound(productId));
	}

	private Optional<Product> getProductById(String productId) {
		return products.get(productId);
	}

	private RuntimeException productNotFound(final String id) {
		throw new RuntimeException(format("product [%s] not found", id));
	}

}
