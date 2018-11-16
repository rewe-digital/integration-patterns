package com.rewedigital.examples.msintegration.productinformation.infrastructure.eventing;

public interface EventSource {
    
    String getId();
    Long getVersion();
    String getAggregateName();

}
