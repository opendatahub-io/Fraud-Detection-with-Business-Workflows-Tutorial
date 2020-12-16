package dev.ruivieira.ccfd.routes.messages.v0;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ruivieira.ccfd.routes.messages.PredictionMetadata;

import java.io.IOException;

@JsonIgnoreProperties(ignoreUnknown=true)
public class PredictionResponse {

    public PredictionData getData() {
        return data;
    }

    @JsonProperty("data")
    private final PredictionData data = new PredictionData();

    public PredictionMetadata getMetadata() {
        return metadata;
    }

    @JsonProperty("meta")
    private final PredictionMetadata metadata = new PredictionMetadata();

    public PredictionResponse() {

    }

    public final static PredictionResponse fromString(String json) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, PredictionResponse.class);
    }

}
