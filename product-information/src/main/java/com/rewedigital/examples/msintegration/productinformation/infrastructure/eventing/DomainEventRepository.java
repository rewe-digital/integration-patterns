package com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.LockModeType;

@NoRepositoryBean
public interface DomainEventRepository<P extends EventPayload, E extends DomainEvent<P>> extends JpaRepository<E, String> {

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    E findOne(String id);
    
    E findFirstByOrderByTimeAsc();

    E findFirstByTimeInSmallestVersion();

}
