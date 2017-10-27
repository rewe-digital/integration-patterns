package com.rewedigital.examples.msintegration.productinformation.product;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ProductEvent {

    @Id
    private String id;

    private String key;

    private LocalDateTime dateTime;

    private String eventType;

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

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(final LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(final String eventType) {
        this.eventType = eventType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(final String payload) {
        this.payload = payload;
    }

    public String toMessage() {
        // TODO Auto-generated method stub
        return null;
    }
}
