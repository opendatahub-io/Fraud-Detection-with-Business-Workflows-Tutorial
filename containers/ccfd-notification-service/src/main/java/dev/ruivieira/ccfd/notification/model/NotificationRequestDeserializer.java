package dev.ruivieira.ccfd.notification.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class NotificationRequestDeserializer implements Deserializer<NotificationRequest> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public NotificationRequest deserialize(String s, byte[] bytes) {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("Notification request deserialization: " + new String(bytes));
        NotificationRequest request = null;
        try {
            request = mapper.readValue(new String(bytes), NotificationRequest.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return request;
    }

    @Override
    public NotificationRequest deserialize(String topic, Headers headers, byte[] data) {
        return this.deserialize(topic, data);
    }

    @Override
    public void close() {

    }
}
