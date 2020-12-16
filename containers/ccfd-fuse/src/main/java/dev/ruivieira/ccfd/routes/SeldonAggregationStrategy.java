package dev.ruivieira.ccfd.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import dev.ruivieira.ccfd.routes.messages.PredictionMetadata;
import dev.ruivieira.ccfd.routes.messages.PredictionMetadata.MetricsData;
import dev.ruivieira.ccfd.routes.messages.v1.PredictionRequest;
import dev.ruivieira.ccfd.routes.model.Classification;
import dev.ruivieira.ccfd.routes.model.Prediction;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class SeldonAggregationStrategy implements AggregationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(SeldonAggregationStrategy.class);

    private final ObjectMapper responseMapper = new ObjectMapper();

    private KieSession kieSession;

    public SeldonAggregationStrategy() {
        responseMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);

        KieServices kieServices = KieServices.Factory.get();
        KieContainer kContainer = kieServices.getKieClasspathContainer();
        kieSession = kContainer.newKieSession();
    }

    public Exchange aggregate(Exchange original, Exchange resource) {
        Object originalBody = original.getIn().getBody();
        Object resourceResponse = resource.getIn().getBody(String.class);

        List<Double> features = new ArrayList<>();

        try {
            ObjectMapper requestMapper = new ObjectMapper();
            Map<String, String> map = requestMapper.readValue(originalBody.toString(), Map.class);
            String[] featureString = map.get("strData").split(",");
            for (String f : featureString) {
                features.add(Double.parseDouble(f));   
            }


            Prediction prediction = new Prediction();

            dev.ruivieira.ccfd.routes.messages.v0.PredictionResponse response = responseMapper.readValue(resourceResponse.toString(), dev.ruivieira.ccfd.routes.messages.v0.PredictionResponse.class);
            PredictionMetadata meta = response.getMetadata();
            List<MetricsData> metrics = meta.getMetrics();
            prediction.setProbability(metrics.get(metrics.size()-1).getValue());

            Classification classification = new Classification();
            kieSession.setGlobal("classification", classification);
            kieSession.insert(prediction);
            kieSession.fireAllRules();

            Map<String, Object> mergeResult = new HashMap<>();

            // generate a random custom id
            mergeResult.put("customer_id", new Random().nextInt(10000));
            // add selected features from the Kaggle dataset
            mergeResult.put("v3", features.get(0));
            mergeResult.put("v4", features.get(1));
            mergeResult.put("v10", features.get(2));
            mergeResult.put("v11", features.get(3));
            mergeResult.put("v12", features.get(4));
            mergeResult.put("v14", features.get(5));
            mergeResult.put("v17", features.get(6));
            mergeResult.put("v29", features.get(7));
            mergeResult.put("fraud_probability", prediction.getProbability());
            mergeResult.put("amount", original.getIn().getHeader("amount"));

            logger.info("Merged payload: " + mergeResult.toString());

            if (original.getPattern().isOutCapable()) {
                original.getMessage().setBody(mergeResult, Map.class);
                original.getMessage().setHeader("fraudulent", classification.isFraudulent());
            } else {
                original.getIn().setBody(mergeResult, Map.class);
                original.getIn().setHeader("fraudulent", classification.isFraudulent());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return original;
    }
}
