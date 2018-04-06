package com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.LockModeType;
import java.util.Optional;

@NoRepositoryBean
public interface DomainEventRepository<E extends DomainEvent> extends JpaRepository<E, String> {

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<E> findById(String id);
    
    E findFirstByOrderByTimeAsc();

    E findFirstByTimeInSmallestVersion();

}
