package com.rewedigital.examples.msintegration.productinformation.product;

import java.time.LocalDateTime;

import javax.persistence.Entity;

import org.springframework.data.annotation.Id;

@Entity
public class ProductEvent {

    @Id
    private String id;

    private String key;

    private LocalDateTime dateTime;

    private String eventType;

    private String payload;

    public String toMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getKey() {
        return key;
    }

}
