package com.rewedigital.examples.msintegration.productinformation.helper;

import com.rewedigital.examples.msintegration.productinformation.product.Product;

public class TestUtil {

    public static Product getTestProduct() {
        final Product product = new Product();
        product.setName("Original Wagner Big Pizza Supreme 420g");
        product.setVendor("Wagner");
        product.setPrice("€ 2,99");
        product.setDescription("Big Taste, Big Fun and Big Pizza...");
        product.setImage("25165635");
        product.setProductNumber("2670536");
        return product;
    }
}
