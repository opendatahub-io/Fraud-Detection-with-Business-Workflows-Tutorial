/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.ruivieira.service.prediction;

import dev.ruivieira.service.messages.PredictionRequest;
import dev.ruivieira.service.messages.PredictionResponse;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.prediction.PredictionOutcome;
import org.kie.internal.task.api.prediction.PredictionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SeldonPredictionService implements PredictionService {

    private static final Logger logger = LoggerFactory.getLogger(SeldonPredictionService.class);
    protected final ResteasyClient client;
    protected final ResteasyWebTarget predict;
    private double confidenceThreshold = 1.0;

    private static final String SELDON_URL_KEY = "SELDON_URL";

    private static final String CONFIDENCE_THRESHOLD_KEY = "CONFIDENCE_THRESHOLD";
    private static final String SELDON_TIMEOUT_KEY = "SELDON_TIMEOUT";
    private static final String SELDON_CONNECTION_POOL_SIZE_KEY = "SELDON_POOL_SIZE";

    private static final String SELDON_ENDPOINT_KEY = "SELDON_ENDPOINT";
    private static final String SELDON_ENDPOINT_DEFAULT = "predict";


    public SeldonPredictionService() {
        final String SELDON_URL = System.getenv(SELDON_URL_KEY);

        // Set the Seldon server endpoint URL
        if (SELDON_URL == null) {
            final String errorMessage = "No Seldon URL specified";
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        // Set the Seldon API URL endpoint
        String SELDON_ENDPOINT = System.getenv(SELDON_ENDPOINT_KEY);

        if (SELDON_ENDPOINT == null) {
            SELDON_ENDPOINT = SELDON_ENDPOINT_DEFAULT;
            logger.info("Using default Seldon endpoint '/predict'");
        }
        logger.debug("Using Seldon endpoint " + SELDON_URL + "/" + SELDON_ENDPOINT);


        ResteasyClientBuilder clientBuilder = new ResteasyClientBuilder();

        final String seldonTimeoutStr = System.getenv(SELDON_TIMEOUT_KEY);

        if (seldonTimeoutStr!=null) {
            try {
                final long seldonTimeout = Long.parseLong(seldonTimeoutStr);
                clientBuilder = clientBuilder.connectionCheckoutTimeout(seldonTimeout, TimeUnit.MILLISECONDS);
                logger.info("Seldon connection timeout set to " + seldonTimeout + " milliseconds");
            } catch (NumberFormatException e) {
                logger.error("Invalid Seldon connection timeout");
            }
        }

        final String seldonConnectioPoolSizeStr = System.getenv(SELDON_CONNECTION_POOL_SIZE_KEY);

        if (seldonConnectioPoolSizeStr!=null) {
            try {
                final int seldonConnectioPoolSize = Integer.parseInt(seldonConnectioPoolSizeStr);
                clientBuilder = clientBuilder.connectionPoolSize(seldonConnectioPoolSize);
                logger.info("Seldon connection pool size set to " + seldonConnectioPoolSize);
            } catch (NumberFormatException e) {
                logger.error("Invalid Seldon connection pool size");
            }
        }

        client = clientBuilder.build();

        predict = client.target(SELDON_URL).path(SELDON_ENDPOINT);

        // set confidence threshold from configuration
        final String CONFIDENCE_THRESHOLD = System.getenv(CONFIDENCE_THRESHOLD_KEY);

        if (CONFIDENCE_THRESHOLD != null) {
            try {
                this.confidenceThreshold = Double.parseDouble(CONFIDENCE_THRESHOLD);
                logger.info("Setting confidence threshold to " + this.confidenceThreshold);
            } catch (NumberFormatException e) {
                logger.error("Invalid confidence threshold in org.jbpm.task.prediction.service.seldon.confidence_threshold");
            }
        } else {
            logger.info("Using default confidence threshold of 1.0");
        }

        logger.debug("Initialization is complete.");
    }

    /**
     * Returns a model prediction given the input data.
     */
    @Override
    public PredictionOutcome predict(Task task, Map<String, Object> map) {
        logger.debug("Building features");
        final List<List<Double>> features = buildPredictFeatures(task, map);
        try {
            logger.debug("Sending a request for " + features);
            final PredictionRequest request = new PredictionRequest();
            request.addFeatures(features.get(0));
            final String json = PredictionRequest.toJSON(request);
            logger.debug("Request JSON " + json);
            final String stringResponse = predict.request(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.entity(json, MediaType.APPLICATION_JSON_TYPE), String.class);
            final PredictionResponse response = PredictionResponse.fromString(stringResponse);
            final Map<String, Object> parsedResponse = parsePredictFeatures(response);
            return new PredictionOutcome((Double) parsedResponse.get("confidence"), this.confidenceThreshold, parsedResponse);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return new PredictionOutcome();
    }

    /**
     * Model training is not supported in this service with Seldon. This is a NO-OP.
     */
    @Override
    public void train(Task task, Map<String, Object> map, Map<String, Object> map1) {
        logger.info("Training is not supported for task: " + task);
    }

    public static final String IDENTIFIER = "SeldonPredictionService";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    /**
     * Build a domain specific list of numerical features based on the input data.
     *
     * @param task Human task data
     * @param map A map containing the input attribute names as keys and the attribute values as values.
     * @return A 2D {@link List} of numerical features
     */

    public List<List<Double>> buildPredictFeatures(Task task, Map<String, Object> map) {
        logger.debug("Got task info: " + task);
        logger.debug("Got a map with " + map);
        List<List<Double>> result = new ArrayList<>();
        List<Double> single = new ArrayList<>();

        single.add((Double) map.get("v3"));
        single.add((Double) map.get("amount"));
        result.add(single);
        logger.debug("The result is " + result);
        return result;
    }

    /**
     * Transfom the deserialised Seldon's response ({@link PredictionResponse}) into a
     * {@link org.kie.internal.task.api.prediction.PredictionOutcome} data map.
     * @param response Deserialized Seldon's response
     * @return A map containing data for {@link org.kie.internal.task.api.prediction.PredictionOutcome}
     */
    public Map<String, Object> parsePredictFeatures(PredictionResponse response) {
        Map<String, Object> result = new HashMap<>();
        List<Double> features = new ArrayList<>();

        if (response.getData().getOutcomes()!=null) {

            features = response.getData().getOutcomes().get(0);

        }

        double o1 = features.get(0);
        double o2 = features.get(1);

        if (o1 > o2) {
            result.put("outcome", 0);
            result.put("confidence", o1);
        } else {
            result.put("outcome", 1);
            result.put("confidence", o2);
        }
        return result;
    }


    public double getConfidenceThreshold() {
        return confidenceThreshold;
    }

    public void setConfidenceThreshold(double confidenceThreshold) {
        this.confidenceThreshold = confidenceThreshold;
    }
}
