package com.rewedigital.examples.msintegration.productinformation.product;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
public class ProductEvent {

    public static ProductEvent of(final Product product, final String eventType, final ObjectMapper objectMapper)
        throws Exception {
        final ProductEvent result = new ProductEvent();
        result.setId(UUID.randomUUID().toString());
        result.setKey(product.getId());
        result.setType(eventType);
        result.setTime(LocalDateTime.now(ZoneOffset.UTC));
        result.setPayload(objectMapper.writeValueAsString(product));
        return result;
    }

    @Id
    private String id;

    private String key;

    private LocalDateTime time;

    private String type;

    private String payload;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(final LocalDateTime time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(final String payload) {
        this.payload = payload;
    }
}
