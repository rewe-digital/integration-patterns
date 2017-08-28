package com.rewedigital.examples.msintegration.productdetailpage.product;

public class Product {
    private String name;
    private String vendor;
    private String price;
    private String description;
    private String productNumber;
    private String image;

    public static class Builder {
        private String name = "";
        private String vendor = "";
        private String price = "€ 0,00";
        private String description = "";
        private String productNumber = "";
        private String image = null;

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

        public Product build() {
            Product result = new Product();
            result.name = this.name;
            result.vendor = this.vendor;
            result.description = this.description;
            result.price = this.price;
            result.image = this.image;
            result.productNumber = this.productNumber;
            return result;
        }
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
}
