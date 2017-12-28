package com.rewedigital.examples.msintegration.productdetailconsumer.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rewedigital.examples.msintegration.productdetailconsumer.infrastructure.eventing.KafkaMessage;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductMessage extends KafkaMessage {

    private final String aggregateName;

    public ProductMessage(@JsonProperty("id") final String id, @JsonProperty("key") final String key,
                          @JsonProperty("time") final String time, @JsonProperty("type") final String type,
                          @JsonProperty("payload") final String payload, @JsonProperty("version") final Integer version,
                          @JsonProperty("aggregateName") final String aggregateName) {
        super(id, key, time, type, payload, version);
        this.aggregateName = aggregateName;
    }

    public String getAggregateName() {
        return aggregateName;
    }
}
