package com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing;

import javax.persistence.Embeddable;
import javax.persistence.Version;

@Embeddable
public class EventPayload {

    @Version
    private Long version;

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
