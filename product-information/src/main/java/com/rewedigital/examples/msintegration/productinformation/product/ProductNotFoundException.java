package com.rewedigital.examples.msintegration.productinformation.product;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(final String message, final String productId) {
        super(String.format(message, productId));
    }
 
    private static final long serialVersionUID = 1L;

}
