package com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface DomainEventRepository extends JpaRepository<DomainEvent, String> {

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    DomainEvent findOne(String id);
    
    DomainEvent findFirstByOrderByTimeAsc();

    @Query(value = "SELECT * FROM DOMAIN_EVENT WHERE key = (SELECT key FROM DOMAIN_EVENT ORDER BY time ASC LIMIT 1) ORDER BY version ASC LIMIT 1", nativeQuery=true)
    DomainEvent findFirstByTimeInSmallestVersion();

}
