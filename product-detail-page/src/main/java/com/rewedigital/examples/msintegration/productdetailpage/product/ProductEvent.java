package com.rewedigital.examples.msintegration.productdetailpage.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.DomainEvent;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductEvent extends DomainEvent<ProductPayload> {

    private final String aggregateName;

    public ProductEvent(@JsonProperty("id") final String id, @JsonProperty("key") final String key,
                        @JsonProperty("time") final String time, @JsonProperty("type") final String type,
                        @JsonProperty("payload") final ProductPayload payload, @JsonProperty("version") final Long version,
                        @JsonProperty("aggregateName") final String aggregateName) {
        super(id, key, time, type, payload, version);
        this.aggregateName = aggregateName;
    }

    public String getAggregateName() {
        return aggregateName;
    }
}
