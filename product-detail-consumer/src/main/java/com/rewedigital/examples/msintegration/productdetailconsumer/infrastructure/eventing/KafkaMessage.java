package com.rewedigital.examples.msintegration.productdetailconsumer.infrastructure.eventing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KafkaMessage {

    @NotNull
    private final String id;

    @NotNull
    private final String key;

    @NotNull
    private final String time;

    @NotNull
    private final String type;

    @NotNull
    private final Integer version;

    @NotNull
    private final String payload;


    public KafkaMessage(@JsonProperty("id") final String id, @JsonProperty("key") final String key,
                        @JsonProperty("time") final String time, @JsonProperty("type") final String type,
                        @JsonProperty("payload") final String payload, @JsonProperty("version") final Integer version) {
        this.id = id;
        this.key = key;
        this.time = time;
        this.type = type;
        this.payload = payload;
        this.version = version;
    }


    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getPayload() {
        return payload;
    }

    public Integer getVersion() {
        return version;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final KafkaMessage that = (KafkaMessage) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(key, that.key)
                .append(time, that.time)
                .append(type, that.type)
                .append(payload, that.payload)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(key)
                .append(time)
                .append(type)
                .append(payload)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "KafkaMessage{" +
                "id='" + id + '\'' +
                ", key='" + key + '\'' +
                ", time='" + time + '\'' +
                ", type='" + type + '\'' +
                ", payload=" + payload +
                '}';
    }
}
