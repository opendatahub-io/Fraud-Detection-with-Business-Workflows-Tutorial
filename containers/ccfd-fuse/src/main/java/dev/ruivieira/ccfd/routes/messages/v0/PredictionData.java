package dev.ruivieira.ccfd.routes.messages.v0;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PredictionData {

    @JsonProperty("names")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> names = new ArrayList<>();

    public void setOutcomes(List<Double> outcomes) {
        this.outcomes = outcomes;
    }

    @JsonProperty("ndarray")
    private List<Double> outcomes = new ArrayList<>();

    public List<String> getNames() {
        return names;
    }

    public List<Double> getOutcomes() {
        return outcomes;
    }

}
