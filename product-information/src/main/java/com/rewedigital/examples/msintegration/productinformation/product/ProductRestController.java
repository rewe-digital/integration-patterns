package com.rewedigital.examples.msintegration.productinformation.product;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing.DomainEvent;

@RestController
public class ProductRestController {

    private static final Logger LOG = LoggerFactory.getLogger(ProductRestController.class);

    private final ProductEventPublishingRepository productRepository;
    private final ObjectMapper objectMapper;

    @Inject
    public ProductRestController(final ProductEventPublishingRepository productRepository,
        final ObjectMapper objectMapper) {
        this.productRepository = Objects.requireNonNull(productRepository);
        this.objectMapper = Objects.requireNonNull(objectMapper);
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
        return productRepository.save(product, e -> toEvent(e, eventType, objectMapper));
    }

    private static DomainEvent toEvent(final Product product, final ProductEventType eventType,
        final ObjectMapper objectMapper) {
        try {
            final DomainEvent result = new DomainEvent();
            result.setId(UUID.randomUUID().toString());
            result.setKey(product.getId());
            result.setType(eventType.getName());
            result.setTime(ZonedDateTime.now(ZoneOffset.UTC));
            result.setPayload(objectMapper.writeValueAsString(product));
            result.setVersion(product.getVersion());
            result.setAggregateName("product");
            return result;
        } catch (final Exception ex) {
            LOG.error("Could not send product event", ex);
            throw new RuntimeException("could not create ProductEvent from Product ", ex);
        }
    }
}
