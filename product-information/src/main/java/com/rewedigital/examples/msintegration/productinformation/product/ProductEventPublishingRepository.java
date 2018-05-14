package com.rewedigital.examples.msintegration.productinformation.product;

import com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing.AbstractEventPublishingRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class ProductEventPublishingRepository extends AbstractEventPublishingRepository<Product, ProductPayload, ProductEvent> {

    private final JpaProductRepository productRepository;

    @Inject
    public ProductEventPublishingRepository(final JpaProductRepository productRepository,
        final ProductEventRepository eventRepository,
        final ApplicationEventPublisher eventPublisher) {
        super(eventRepository, eventPublisher);
        this.productRepository = Objects.requireNonNull(productRepository);
    }

    @Override
    protected Product persist(final Product entity) {
        return productRepository.saveAndFlush(entity);
    }

    public Optional<Product> findById(final String id) {
        return productRepository.findById(id);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

}
