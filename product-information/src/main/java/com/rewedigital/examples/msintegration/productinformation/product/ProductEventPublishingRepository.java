package com.rewedigital.examples.msintegration.productinformation.product;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing.AbstractEventPublishingRepository;
import com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing.DomainEventRepository;

@Component
public class ProductEventPublishingRepository extends AbstractEventPublishingRepository<Product> {

    private final JpaProductRepository productRepository;

    @Inject
    public ProductEventPublishingRepository(final JpaProductRepository productRepository,
        final DomainEventRepository eventRepository,
        final ApplicationEventPublisher eventPublisher) {
        super(eventRepository, eventPublisher);
        this.productRepository = Objects.requireNonNull(productRepository);
    }

    @Override
    protected Product persist(final Product entity) {
        return productRepository.saveAndFlush(entity);
    }

    public Product findOne(final String id) {
        return productRepository.findOne(id);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

}
