package com.rewedigital.examples.msintegration.productinformation.product;

public enum ProductEventType {
    PRODUCT_CREATED("product-created"), PRODUCT_UPDATED("product-updated");

    private String name;

    ProductEventType(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
