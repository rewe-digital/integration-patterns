package com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing;

import org.springframework.context.ApplicationEvent;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
public class DomainEvent {
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

    @Id
    private String id;

    private Long version;

    private String key;

    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime time;

    private String type;

    @Lob
    private byte[] payload;

    private String aggregateName;

    private Class<?> entityType;

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

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(final byte[] payload) {
        this.payload = payload;
    }

    public Class<?> getEntityType() {
        return entityType;
    }

    public void setEntityType(Class<?> entityType) {
        this.entityType = entityType;
    }

    public String getAggregateName() {
        return aggregateName;
    }

    public void setAggregateName(final String aggregateName) {
        this.aggregateName = aggregateName;
    }
}
