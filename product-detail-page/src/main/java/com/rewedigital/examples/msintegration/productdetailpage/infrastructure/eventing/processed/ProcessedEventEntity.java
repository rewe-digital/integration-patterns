package com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.processed;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames = {"key", "version", "topic"})})
public class ProcessedEventEntity {

    public ProcessedEventEntity(String key, String topic, Long version) {
        this.key = key;
        this.topic = topic;
        this.version = version;
    }

    @Id
    @GeneratedValue
    private Long id;
    private String key;
    private Long version;
    private String topic;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
