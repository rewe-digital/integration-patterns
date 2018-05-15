package com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface DomainEventRepository extends JpaRepository<DomainEvent, String> {

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<DomainEvent> findById(String id);
    
    DomainEvent findFirstByOrderByTimeAsc();

    @Query(value = "SELECT * FROM DOMAIN_EVENT WHERE key = (SELECT key FROM PRODUCT_EVENT ORDER BY time ASC LIMIT 1) ORDER BY version ASC LIMIT 1", nativeQuery=true)
    DomainEvent findFirstByTimeInSmallestVersion();

}
