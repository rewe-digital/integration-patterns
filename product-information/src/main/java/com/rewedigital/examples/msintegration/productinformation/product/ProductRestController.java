package com.rewedigital.examples.msintegration.productinformation.product;

import java.util.Objects;

import javax.inject.Inject;

import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductRestController {

    private final ProductRepository productRepository;

    @Inject
    public ProductRestController(final ProductRepository productRepository) {
        this.productRepository = Objects.requireNonNull(productRepository);
    }

    @RequestMapping(value = "/products", method = RequestMethod.PUT )
    public Product addProduct(final Product product) {

    }

}
