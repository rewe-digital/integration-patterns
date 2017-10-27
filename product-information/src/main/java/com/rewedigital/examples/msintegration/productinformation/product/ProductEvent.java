package com.rewedigital.examples.msintegration.productinformation.product;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.context.ApplicationEvent;

import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
public class ProductEvent {

    public static class Message extends ApplicationEvent {
        private static final long serialVersionUID = 1L;
        private final String id;

        private Message(final String id, final Object source) {
            super(source);
            this.id = id;
        }

        public String id() {
            return id;
        }
    }

    public static ProductEvent of(final Product product, final ProductEventType eventType,
        final ObjectMapper objectMapper)
        throws Exception {
        final ProductEvent result = new ProductEvent();
        result.setId(UUID.randomUUID().toString());
        result.setKey(product.getId());
        result.setType(eventType.getName());
        result.setTime(ZonedDateTime.now(ZoneOffset.UTC));
        result.setPayload(objectMapper.writeValueAsString(product));
        return result;
    }

    @Id
    private String id;

    private String key;

    private ZonedDateTime time;

    private String type;

    private String payload;

    public Message message(final Object source) {
        return new Message(id, source);
    }

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

    public ZonedDateTime getTime() {
        return time;
    }

    public void setTime(final ZonedDateTime time) {
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