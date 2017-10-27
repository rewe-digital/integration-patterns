package com.rewedigital.examples.msintegration.productinformation.product;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductRestController {

    private final ProductRepository productRepository;

    @Inject
    public ProductRestController(final ProductRepository productRepository) {
        this.productRepository = Objects.requireNonNull(productRepository);
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
        
        return productRepository.save(product);
    }

    @RequestMapping(value = "/products", method = RequestMethod.GET)
    public List<Product> getAll() {
        return productRepository.findAll();
    }

}
