package dev.ruivieira.ccfd.notification.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class NotificationResponseDeserializer implements Deserializer<NotificationResponse> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public NotificationResponse deserialize(String s, byte[] bytes) {

        ObjectMapper mapper = new ObjectMapper();
        NotificationResponse value = null;
        try {
            value = mapper.readValue(new String(bytes), NotificationResponse.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return value;
    }

    @Override
    public NotificationResponse deserialize(String topic, Headers headers, byte[] data) {

        return this.deserialize(topic, data);
    }

    @Override
    public void close() {

    }
}
