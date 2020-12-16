package dev.ruivieira.ccfd.routes.messages;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.ruivieira.ccfd.routes.messages.v0.PredictionRequest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class PredictionRequestTest {

    @Test
    void fromString() throws IOException {
        String payload = "{\"data\":{\"ndarray\":[881.0]}}";
        PredictionRequest request = PredictionRequest.fromString(payload);
        System.out.println(request.getData().getOutcomes());
    }

    @Test
    void toJSON() throws JsonProcessingException {
        PredictionRequest request = new PredictionRequest();
        List<Double> features = new ArrayList<>();
        features.add(881.0);
        features.add(47.76);
        request.setFeatures(features);
        System.out.println(PredictionRequest.toJSON(request));
    }
}