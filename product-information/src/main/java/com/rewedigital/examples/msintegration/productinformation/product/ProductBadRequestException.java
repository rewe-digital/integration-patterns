package com.rewedigital.examples.msintegration.productinformation.product;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST)
public class ProductBadRequestException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ProductBadRequestException(final String message) {
        super(message);
    }
}
