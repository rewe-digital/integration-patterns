package com.rewedigital.examples.msintegration.productinformation.product;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ProductLastPublishedVersion {

    @Id
    private String id;
    private Long version;

    public static ProductLastPublishedVersion of(final String productId) {
        return new ProductLastPublishedVersion(productId);
    }

    public ProductLastPublishedVersion() {
        this(null);
    }

    private ProductLastPublishedVersion(final String productId) {
        this.id = productId;
        this.version = -1L;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(final Long version) {
        this.version = version;
    }
}
