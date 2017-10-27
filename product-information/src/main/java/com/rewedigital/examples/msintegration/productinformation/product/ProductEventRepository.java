package com.rewedigital.examples.msintegration.productinformation.product;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface ProductEventRepository extends JpaRepository<ProductEvent, String> {

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    ProductEvent findOne(String id);

}
