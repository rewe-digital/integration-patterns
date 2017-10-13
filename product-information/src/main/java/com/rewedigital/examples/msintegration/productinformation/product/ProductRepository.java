package com.rewedigital.examples.msintegration.productinformation.product;


import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {

}
