package com.rewedigital.examples.msintegration.productinformation.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;

public interface ProductEventRepository extends JpaRepository<ProductEvent, String> {

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    ProductEvent findOne(String id);
    
    ProductEvent findFirstByOrderByTimeAsc();

    @Query(value = "SELECT * FROM PRODUCT_EVENT WHERE key = (SELECT key FROM PRODUCT_EVENT ORDER BY time ASC LIMIT 1) ORDER BY version ASC LIMIT 1", nativeQuery=true)
    ProductEvent findFirstByTimeInSmallestVersion();

}
