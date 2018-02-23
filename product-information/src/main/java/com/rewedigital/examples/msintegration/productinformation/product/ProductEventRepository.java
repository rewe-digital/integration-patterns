package com.rewedigital.examples.msintegration.productinformation.product;

import com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing.DomainEventRepository;
import org.springframework.data.jpa.repository.Query;


public interface ProductEventRepository extends DomainEventRepository<ProductEvent> {

    @Query(value = "SELECT * FROM PRODUCT_EVENT WHERE key = (SELECT key FROM PRODUCT_EVENT ORDER BY time ASC LIMIT 1) ORDER BY version ASC LIMIT 1", nativeQuery=true)
    ProductEvent findFirstByTimeInSmallestVersion();
}
