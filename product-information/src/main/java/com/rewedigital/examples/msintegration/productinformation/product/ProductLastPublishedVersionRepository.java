package com.rewedigital.examples.msintegration.productinformation.product;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface ProductLastPublishedVersionRepository extends JpaRepository<ProductLastPublishedVersion, String> {

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    ProductLastPublishedVersion findOne(String id);

}
