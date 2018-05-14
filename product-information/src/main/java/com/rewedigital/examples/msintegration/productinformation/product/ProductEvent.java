package com.rewedigital.examples.msintegration.productinformation.product;

import com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing.DomainEvent;

import javax.persistence.Entity;

@Entity
public class ProductEvent extends DomainEvent<ProductPayload> {
}
