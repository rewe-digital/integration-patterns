package com.rewedigital.examples.msintegration.productinformation.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
public class ProductRestController {

    private static final Logger LOG = LoggerFactory.getLogger(ProductRestController.class);

    private final ProductEventPublishingRepository productEventPublishingRepository;
    private final ObjectMapper objectMapper;

    @Inject
    public ProductRestController(final ProductEventPublishingRepository productEventPublishingRepository,
        final ObjectMapper objectMapper) {
        this.productEventPublishingRepository = Objects.requireNonNull(productEventPublishingRepository);
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
        productEventPublishingRepository
            .findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("product with id %s does not exist", productId));

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
        return productEventPublishingRepository.findAll();
    }

    private Product persist(final Product product, final ProductEventType eventType) {
        return productEventPublishingRepository.save(product, e -> toEvent(e, eventType, objectMapper));
    }

    private static ProductEvent toEvent(final Product product, final ProductEventType eventType,
        final ObjectMapper objectMapper) {
        try {
            final ProductEvent result = new ProductEvent();
            result.setId(UUID.randomUUID().toString());
            result.setKey(product.getId());
            result.setType(eventType.getName());
            result.setTime(ZonedDateTime.now(ZoneOffset.UTC));
            result.setPayload(product.toPayload());
            result.setVersion(product.getVersion());
            result.setAggregateName("product");
            return result;
        } catch (final Exception ex) {
            LOG.error("Could not send product event", ex);
            throw new RuntimeException("could not create ProductEvent from Product ", ex);
        }
    }
}
