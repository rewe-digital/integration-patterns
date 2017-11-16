package com.rewedigital.examples.msintegration.productinformation.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationEvent;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

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
        result.setVersion(product.getVersion());
        return result;
    }

    @Id
    private String id;

    private Long version;

    private String key;

    @Convert(converter = ZonedDateTimeConverter.class)
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(final Long version) {
        this.version = version;
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
