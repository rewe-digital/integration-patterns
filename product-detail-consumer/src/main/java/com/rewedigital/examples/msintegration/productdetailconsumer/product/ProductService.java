package com.rewedigital.examples.msintegration.productdetailconsumer.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);

    //TODO: implement
    public void createOrUpdateProduct(ProductMessage productMessage) {
        LOG.info("ProductService.createOrUpdateProduct()");
    }
}
