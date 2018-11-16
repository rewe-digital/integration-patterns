package com.rewedigital.examples.msintegration.productinformation.product;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.inject.Inject;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class ProductRestController {

    private final JpaProductRepository productRepository;

    @Inject
    public ProductRestController(final JpaProductRepository productRepository, final ObjectMapper objectMapper) {
        this.productRepository = Objects.requireNonNull(productRepository);
    }

    @RequestMapping(value = "/products", method = RequestMethod.POST)
    @Transactional
    public Product addProduct(@RequestBody final Product product) {
        if (product.getId() != null) {
            throw new ProductBadRequestException("Must not provide id");
        }

        product.setId(UUID.randomUUID().toString());
        return productRepository.save(product);
    }

    @RequestMapping(value = "/products/{productId}", method = RequestMethod.PUT)
    @Transactional
    public Product updateProduct(@PathVariable final String productId, @RequestBody final Product product) {
        productRepository
            .findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("product with id %s does not exist", productId));

        if (!productId.equals(product.getId())) {
            throw new ProductBadRequestException("wrong id in payload");
        }

        if (product.getVersion() == null) {
            throw new ProductBadRequestException("missing version attribute");
        }

        return productRepository.save(product);
    }

    @RequestMapping(value = "/products", method = RequestMethod.GET)
    public List<Product> getAll() {
        return productRepository.findAll();
    }

}
