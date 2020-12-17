package org.jbpm.contrib.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class PredictionData {

    @JsonProperty("names")
    private final List<String> names = new ArrayList<>();

    @JsonProperty("ndarray")
    private final List<List<Double>> outcomes = new ArrayList<>();

    public List<String> getNames() {
        return names;
    }

    public List<List<Double>> getOutcomes() {
        return outcomes;
    }

}
