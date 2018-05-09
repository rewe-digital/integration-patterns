package com.rewedigital.examples.msintegration.productdetailpage.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.EventPayload;

import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductPayload extends EventPayload {

    private String productId;
    private String name;
    private String vendor;
    private String price;
    private String description;
    @NotNull
    private String productNumber;
    private String image;

    public void setProductId(final String productId) {
        this.productId = productId;
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

    public String getProductId() {
        return productId;
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
