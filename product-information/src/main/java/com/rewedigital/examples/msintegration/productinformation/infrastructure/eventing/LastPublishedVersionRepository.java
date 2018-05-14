package com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface LastPublishedVersionRepository extends JpaRepository<LastPublishedVersion, String> {

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<LastPublishedVersion> findById(String id);

}
