package com.rewedigital.examples.msintegration.productinformation.product;

import com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing.AbstractEventPublishingRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;

@Component
public class ProductEventPublishingRepository extends AbstractEventPublishingRepository<Product> {

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

    public Product findOne(final String id) {
        return productRepository.findOne(id);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

}
