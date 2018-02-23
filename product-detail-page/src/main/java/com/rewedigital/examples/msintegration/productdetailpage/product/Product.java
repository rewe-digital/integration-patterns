package com.rewedigital.examples.msintegration.productdetailpage.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Entity
public class Product {

    @Id
    private String id;

    @NotNull
    private String name;
    private String vendor;
    private String price;

    @Column(length=2000)
    private String description;

    @NotNull
    private String productNumber;
    private String image;
    
    @Version
    private Long version;

    public void setId(final String id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setVendor(final String vendor) {
        this.vendor = vendor;
    }

    public void setPrice(final String price) {
        this.price = price;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setProductNumber(final String productNumber) {
        this.productNumber = productNumber;
    }

    public void setImage(final String image) {
        this.image = image;
    }

    public void setVersion(final Long version) {
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVendor() {
        return vendor;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public Long getVersion() {
        return version;
    }

    public static class Builder {
        private String name = "";
        private String vendor = "";
        private String price = "€ 0,00";
        private String description = "";
        private String productNumber = "";
        private String image = null;

        public com.rewedigital.examples.msintegration.productdetailpage.product.Product.Builder withName(String name) {
            this.name = name;
            return this;
        }

        public com.rewedigital.examples.msintegration.productdetailpage.product.Product.Builder withVendor(String vendor) {
            this.vendor = vendor;
            return this;
        }

        public com.rewedigital.examples.msintegration.productdetailpage.product.Product.Builder withPrice(String price) {
            this.price = price;
            return this;
        }

        public com.rewedigital.examples.msintegration.productdetailpage.product.Product.Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public com.rewedigital.examples.msintegration.productdetailpage.product.Product.Builder withProductNumber(String number) {
            this.productNumber = number;
            return this;
        }

        public com.rewedigital.examples.msintegration.productdetailpage.product.Product.Builder withImage(String image) {
            this.image = image;
            return this;
        }

        public com.rewedigital.examples.msintegration.productdetailpage.product.Product build() {
            com.rewedigital.examples.msintegration.productdetailpage.product.Product result = new com.rewedigital.examples.msintegration.productdetailpage.product.Product();
            result.name = this.name;
            result.vendor = this.vendor;
            result.description = this.description;
            result.price = this.price;
            result.image = this.image;
            result.productNumber = this.productNumber;
            return result;
        }
    }
}
