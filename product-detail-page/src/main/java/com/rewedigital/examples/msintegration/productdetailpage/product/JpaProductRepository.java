package com.rewedigital.examples.msintegration.productdetailpage.product;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaProductRepository extends JpaRepository<Product, String> {
    Optional<Product> findByProductNumber(String productNumber);

}
