package com.rewedigital.examples.msintegration.productdetailpage.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class ProductService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);

    private final JpaProductRepository repository;

    @Inject
    public ProductService(JpaProductRepository repository) {
        this.repository = repository;
    }

    public void createOrUpdateProduct(Product product) {
        repository.save(product);
    }
}
