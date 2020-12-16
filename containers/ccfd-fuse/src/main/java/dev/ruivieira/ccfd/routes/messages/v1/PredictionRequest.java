package dev.ruivieira.ccfd.routes.messages.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class PredictionRequest implements Serializable {

    @JsonProperty("data")
    private PredictionData data = new PredictionData();

    public PredictionRequest() {
    }

    public void addFeatures(List<Double> features) {
        this.data.getOutcomes().add(features);
    }

    public final static PredictionRequest fromString(String json) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, PredictionRequest.class);
    }

    public final static String toJSON(PredictionRequest request) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(request);
    }

    public PredictionData getData() {
        return data;
    }
}