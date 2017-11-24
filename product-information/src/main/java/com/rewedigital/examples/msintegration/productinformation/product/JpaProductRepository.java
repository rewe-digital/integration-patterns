package com.rewedigital.examples.msintegration.productinformation.product;


import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProductRepository extends JpaRepository<Product, String> {

}
