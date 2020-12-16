package dev.ruivieira.ccfd.routes.messages;

import org.junit.jupiter.api.Test;

import dev.ruivieira.ccfd.routes.messages.PredictionMetadata.MetricsData;
import dev.ruivieira.ccfd.routes.messages.v0.PredictionResponse;

import java.io.IOException;
import java.util.List;

class PredictionResponseTest {

    @Test
    void fromString() throws IOException {
        String payload = "{\"data\":{\"names\":[\"toto\"],\"tensor\":{\"shape\":[1],\"values\":[0]}},\"meta\":{\"metrics\":[{\"key\":\"mycounter\",\"type\":\"COUNTER\",\"value\":1},{\"key\":\"mygauge\",\"type\":\"GAUGE\",\"value\":100},{\"key\":\"mytimer\",\"type\":\"TIMER\",\"value\":20.2},{\"key\":\"V3\",\"type\":\"GAUGE\",\"value\":1.759247460267},{\"key\":\"V4\",\"type\":\"GAUGE\",\"value\":-0.359744743330052},{\"key\":\"V10\",\"type\":\"GAUGE\",\"value\":-0.238253367661746},{\"key\":\"V11\",\"type\":\"GAUGE\",\"value\":-1.52541162656194},{\"key\":\"V12\",\"type\":\"GAUGE\",\"value\":2.03291215755072},{\"key\":\"V14\",\"type\":\"GAUGE\",\"value\":0.0229373234890961},{\"key\":\"V17\",\"type\":\"GAUGE\",\"value\":-2.28219382856251},{\"key\":\"Amount\",\"type\":\"GAUGE\",\"value\":-0.153028796529788},{\"key\":\"proba_1\",\"type\":\"GAUGE\",\"value\":0.04573083222892336}]}}";
        PredictionResponse response = PredictionResponse.fromString(payload);
        PredictionMetadata meta = response.getMetadata();
        List<MetricsData> metrics = meta.getMetrics();
        System.out.println(metrics.get(metrics.size()-1).getValue());
    }
}