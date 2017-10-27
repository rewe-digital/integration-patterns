package com.rewedigital.examples.msintegration.productinformation.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
public class ProductRestController {

    private final ProductRepository productRepository;
    private final ProductEventRepository productEventRepository;

    @Inject
    public ProductRestController(final ProductRepository productRepository, final ProductEventRepository productEventRepository) {
        this.productRepository = Objects.requireNonNull(productRepository);
        this.productEventRepository = Objects.requireNonNull(productEventRepository);
    }

    @RequestMapping(value = "/products", method = RequestMethod.POST)
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
        final Product foundProduct = productRepository.findOne(productId);
        if (foundProduct == null) {
            throw new ProductNotFoundException("product with id %s does not exist", productId);
        } 
        
        if(!productId.equals(product.getId())) {
            throw new ProductBadRequestException("wrong id in payload");
        }
        
        if(product.getVersion() == null) {
            throw new ProductBadRequestException("missing version attribute");
        }

        Product persistentProduct = productRepository.save(product);
        try {
            ProductEvent productEvent = ProductEvent.of(persistentProduct, ProductEventType.PRODUCT_CREATED.toString(), new ObjectMapper());
            productEventRepository.save(productEvent);
        }
        catch (Exception e) {
            throw new RuntimeException("could not create ProductEvent from Product ", e);
        }

        return persistentProduct;
    }

    @RequestMapping(value = "/products", method = RequestMethod.GET)
    public List<Product> getAll() {
        return productRepository.findAll();
    }

}
