package com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing.internal;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class LastPublishedVersion {

    @Id
    private String id;
    private Long version;

    public static LastPublishedVersion of(final String id) {
        return new LastPublishedVersion(id);
    }

    public LastPublishedVersion() {
        this(null);
    }

    private LastPublishedVersion(final String id) {
        this.id = id;
        this.version = -1L;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(final Long version) {
        this.version = version;
    }
}
