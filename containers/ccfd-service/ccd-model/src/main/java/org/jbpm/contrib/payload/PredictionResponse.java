package org.jbpm.contrib.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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

}
