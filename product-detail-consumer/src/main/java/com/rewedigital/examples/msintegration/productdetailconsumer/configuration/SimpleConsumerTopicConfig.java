package com.rewedigital.examples.msintegration.productdetailconsumer.configuration;

import com.rewedigital.examples.msintegration.productdetailconsumer.infrastructure.eventing.ConsumerTopicConfig;

public class SimpleConsumerTopicConfig implements ConsumerTopicConfig {

    private String name;
    private boolean payloadSensitive = false;

    @Override
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public boolean isPayloadSensitive() {
        return payloadSensitive;
    }

    public void setPayloadSensitive(final boolean payloadSensitive) {
        this.payloadSensitive = payloadSensitive;
    }

}
