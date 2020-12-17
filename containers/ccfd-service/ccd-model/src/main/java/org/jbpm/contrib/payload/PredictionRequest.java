package org.jbpm.contrib.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonRootName(value = "data")
public class PredictionRequest implements Serializable {

    private final List<List<Double>> features;

    public PredictionRequest() {
        this(new ArrayList<>());
    }

    public PredictionRequest(List<List<Double>> features) {
        this.features = features;
    }

    @JsonProperty("ndarray")
    public List<List<Double>> getFeatures() {
        return features;
    }

    public void addFeatures(List<Double> features) {
        this.features.add(features);
    }
}
