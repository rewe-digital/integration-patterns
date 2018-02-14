package com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class ConsumerRecordLoggingHelper {

    public static String toLogSafeString(final ConsumerRecord<String, String> record) {
        return toLogSafeString(record, false);
    }

    public static String toLogSafeString(final ConsumerRecord<String, String> record, final boolean includePayload) {
        final ToStringBuilder builder = new ToStringBuilder(record, ToStringStyle.JSON_STYLE,
                new StringBuffer(record.getClass().getSimpleName()).append(" "))
                .append("topic", record.topic())
                .append("partition", record.partition())
                .append("offset", record.offset())
                .append("key", record.key());
        if (includePayload) {
            builder.append("value", record.value());
        }
        return builder.toString();
    }

}
