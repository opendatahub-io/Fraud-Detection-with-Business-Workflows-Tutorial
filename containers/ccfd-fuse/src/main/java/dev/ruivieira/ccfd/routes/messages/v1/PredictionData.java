package dev.ruivieira.ccfd.routes.messages.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

    @JsonIgnoreProperties(ignoreUnknown=true)
    public class PredictionData {

        @JsonProperty("names")
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private List<String> names = new ArrayList<>();

        @JsonProperty("ndarray")
        private final List<List<Double>> outcomes = new ArrayList<>();

        public List<String> getNames() {
            return names;
        }

        public List<List<Double>> getOutcomes() {
            return outcomes;
        }

    }

