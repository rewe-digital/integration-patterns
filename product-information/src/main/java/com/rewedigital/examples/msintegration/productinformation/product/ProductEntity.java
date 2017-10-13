package com.rewedigital.examples.msintegration.productinformation.product;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ProductEntity {

    @Id
    private String id;

    private String name;
    private String vendor;
    private String price;
    private String description;
    private String productNumber;
    private String image;
    private long version;

    public static ProductEntity of(final Product product) {
        ProductEntity productEntity = new ProductEntity();

        productEntity.name = product.getName();
        productEntity.vendor = product.getVendor();
        productEntity.price = product.getPrice();
        productEntity.description = product.getDescription();
        productEntity.productNumber = product.getProductNumber();
        productEntity.image = product.getImage();
        productEntity.version = product.getVersion()

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
