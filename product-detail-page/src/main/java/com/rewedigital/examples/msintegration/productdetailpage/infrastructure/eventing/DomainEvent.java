package com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing;

//import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DomainEvent<P extends EventPayload> {

 //   @NotNull
    private final String id;

//    @NotNull
    private final String key;

//    @NotNull
    private final String time;

//    @NotNull
    private final String type;

//    @NotNull
    private final Long version;

//    @NotNull
    private final P payload;


    public DomainEvent(@JsonProperty("id") final String id, @JsonProperty("key") final String key,
                       @JsonProperty("time") final String time, @JsonProperty("type") final String type,
                       @JsonProperty("payload") final P payload, @JsonProperty("version") final Long version) {
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

    public P getPayload() {
        return payload;
    }

    public Long getVersion() {
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

        @SuppressWarnings("unchecked")
        final DomainEvent<P> that = (DomainEvent<P>) o;

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
        return "DomainEvent{" +
                "id='" + id + '\'' +
                ", key='" + key + '\'' +
                ", time='" + time + '\'' +
                ", type='" + type + '\'' +
                ", payload=" + payload +
                '}';
    }
}
