package com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.unprocessable;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import javax.persistence.Entity;
import javax.persistence.Id;
//import javax.validation.constraints.NotNull;

@Entity
public class UnprocessedEventEntity {

    @Id
    private String key;

//    @NotNull
    private String topic;

//    @NotNull
    private String payload;

    public UnprocessedEventEntity() {
        super();
    }

    public UnprocessedEventEntity(ConsumerRecord<String, String> consumerRecord) {
        this.key=consumerRecord.key();
        this.payload= consumerRecord.value();
        this.topic = consumerRecord.topic();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
