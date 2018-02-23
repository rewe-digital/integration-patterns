package com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface LastPublishedVersionRepository extends JpaRepository<LastPublishedVersion, String> {

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    LastPublishedVersion findOne(String id);

}
