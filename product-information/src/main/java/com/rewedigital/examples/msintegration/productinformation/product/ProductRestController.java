package com.rewedigital.examples.msintegration.productinformation.product;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.inject.Inject;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class ProductRestController {

    private final ProductRepository productRepository;
    private final ProductEventRepository productEventRepository;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Inject
    public ProductRestController(final ProductRepository productRepository,
        final ProductEventRepository productEventRepository, final ObjectMapper objectMapper,
        final ApplicationEventPublisher applicationEventPublisher) {
        this.productRepository = Objects.requireNonNull(productRepository);
        this.productEventRepository = Objects.requireNonNull(productEventRepository);
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.applicationEventPublisher = Objects.requireNonNull(applicationEventPublisher);
    }

    @RequestMapping(value = "/products", method = RequestMethod.POST)
    @Transactional
    public Product addProduct(@RequestBody final Product product) {
        if (product.getId() != null) {
            throw new ProductBadRequestException("Must not provide id");
        }

        product.setId(UUID.randomUUID().toString());
        return persist(product, ProductEventType.PRODUCT_CREATED);
    }



    @RequestMapping(value = "/products/{productId}", method = RequestMethod.PUT)
    @Transactional
    public Product updateProduct(@PathVariable final String productId, @RequestBody final Product product) {
        final Product foundProduct = productRepository.findOne(productId);
        if (foundProduct == null) {
            throw new ProductNotFoundException("product with id %s does not exist", productId);
        }

        if (!productId.equals(product.getId())) {
            throw new ProductBadRequestException("wrong id in payload");
        }

        if (product.getVersion() == null) {
            throw new ProductBadRequestException("missing version attribute");
        }

        return persist(product, ProductEventType.PRODUCT_UPDATED);
    }

    @RequestMapping(value = "/products", method = RequestMethod.GET)
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    private Product persist(final Product product, final ProductEventType eventType) {
        final Product persistentProduct = productRepository.save(product);
        try {
            final ProductEvent productEvent =
                productEventRepository.save(ProductEvent.of(persistentProduct, eventType, objectMapper));
            applicationEventPublisher.publishEvent(productEvent.message(this));
        } catch (final Exception e) {
            throw new RuntimeException("could not create ProductEvent from Product ", e);
        }

        return persistentProduct;
    }
}
