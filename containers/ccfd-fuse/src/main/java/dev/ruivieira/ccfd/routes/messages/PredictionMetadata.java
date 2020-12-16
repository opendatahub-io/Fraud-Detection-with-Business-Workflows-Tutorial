package dev.ruivieira.ccfd.routes.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown=true)
public class PredictionMetadata {

    public static class MetricsData {
        private String key;
        private String type;
        private Double value;

        public String getKey() {
            return this.key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getType() {
            return this.type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Double getValue() {
            return this.value;
        }

        public void setValue(Double value) {
            this.value = value;
        }
    }

    @JsonProperty("metrics")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<MetricsData> metricsdata = new ArrayList<>();
    

    public List<MetricsData> getMetrics() {
        return metricsdata;
    }

}
