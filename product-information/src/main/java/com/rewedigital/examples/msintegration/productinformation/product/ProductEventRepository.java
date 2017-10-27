package com.rewedigital.examples.msintegration.productinformation.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;

public interface ProductEventRepository extends JpaRepository<ProductEvent, String> {

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    ProductEvent findOne(String id);
    
    ProductEvent findFirstByOrderByTimeAsc();


}
