package dev.ruivieira.ccfd.notification.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class NotificationResponseSerializer implements Serializer<NotificationResponse> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String s, NotificationResponse notificationRequest) {

        ObjectMapper mapper = new ObjectMapper();
        String json = "{}";
        try {
            json = mapper.writeValueAsString(notificationRequest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json.getBytes();
    }

    @Override
    public byte[] serialize(String topic, Headers headers, NotificationResponse data) {

        return this.serialize(topic, data);
    }

    @Override
    public void close() {

    }
}
