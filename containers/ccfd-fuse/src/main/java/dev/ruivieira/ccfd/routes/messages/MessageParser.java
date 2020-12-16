package dev.ruivieira.ccfd.routes.messages;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageParser {

    public static List<String> parseV0(String payload) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(payload, ArrayList.class);
        } catch (IOException e) {
            // TODO: user logger. Handle exception
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static List<String> parseV1(String payload) {
        return Arrays.asList(payload.split(","));
    }

}
