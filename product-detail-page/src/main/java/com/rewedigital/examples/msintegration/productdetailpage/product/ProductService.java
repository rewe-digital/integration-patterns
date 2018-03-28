package com.rewedigital.examples.msintegration.productdetailpage.product;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

@Service
public class ProductService {

	private final JpaProductRepository repository;

    @Inject
    public ProductService(JpaProductRepository repository) {
        this.repository = repository;
    }

    public void createOrUpdateProduct(Product product) {
        repository.save(product);
    }
}
