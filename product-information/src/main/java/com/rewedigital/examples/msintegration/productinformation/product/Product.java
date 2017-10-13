package com.rewedigital.examples.msintegration.productinformation.product;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Product {
    @Id
    private String id;
    private String name;
    private String vendor;
    private String price;
    private String description;
    private String productNumber;
    private String image;
    private long version;

    public static class Builder {
        private String id = "";
        private String name = "";
        private String vendor = "";
        private String price = "€ 0,00";
        private String description = "";
        private String productNumber = "";
        private String image = null;
        private long version = 0;

        public Builder withId(final String id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withVendor(String vendor) {
            this.vendor = vendor;
            return this;
        }

        public Builder withPrice(String price) {
            this.price = price;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withProductNumber(String number) {
            this.productNumber = number;
            return this;
        }

        public Builder withImage(String image) {
            this.image = image;
            return this;
        }

        public Builder withVersion(long version) {
            this.version = version;
            return this;
        }

        public Product build() {
            Product result = new Product();
            result.id = this.id;
            result.name = this.name;
            result.vendor = this.vendor;
            result.description = this.description;
            result.price = this.price;
            result.image = this.image;
            result.productNumber = this.productNumber;
            result.version = this.version;
            return result;
        }
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

    public long getVersion() {
        return version;
    }
}
