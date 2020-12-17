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
package org.jbpm.contrib;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jbpm.contrib.payload.PredictionRequest;
import org.jbpm.contrib.payload.PredictionResponse;
import org.jbpm.process.workitem.core.AbstractLogOrThrowWorkItemHandler;
import org.jbpm.process.workitem.core.util.RequiredParameterValidator;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeldonWorkItemHandler extends AbstractLogOrThrowWorkItemHandler {

    protected final ResteasyClient client = new ResteasyClientBuilder().build();
    protected final ResteasyWebTarget predict;

    private static final Logger logger = LoggerFactory.getLogger(SeldonWorkItemHandler.class);

    private final String SELDON_TOKEN;
    private final boolean USE_SELDON_TOKEN;

    private final String ENDPOINT = "/predict";

    public SeldonWorkItemHandler() {
        final String SELDON_URL = System.getenv("SELDON_URL");

        SELDON_TOKEN = System.getenv("SELDON_TOKEN");

        USE_SELDON_TOKEN = SELDON_TOKEN != null;

        if (USE_SELDON_TOKEN) {
            logger.debug("Using Seldon token " + SELDON_TOKEN);
        } else {
            logger.debug("Seldon token not set");
        }

        if (SELDON_URL==null) {
            logger.error("No Seldon URL provided");
            throw new IllegalArgumentException("No Seldon URL provided.");
        }

        predict = client.target(SELDON_URL).path(ENDPOINT);
    }

    public void executeWorkItem(WorkItem workItem,
                                WorkItemManager manager) {
        try {
            RequiredParameterValidator.validate(this.getClass(),
               workItem);
            logger.debug(workItem.toString());
            // sample parameters
            final List<List<Double>> features = new ArrayList<>();
            final List<Double> feature = new ArrayList<>();
            final Integer sampleParam = (Integer) workItem.getParameter("accountId");
            feature.add(Double.valueOf(sampleParam));
            final Double sampleParamTwo = (Double) workItem.getParameter("transactionAmount");
            feature.add(sampleParamTwo);
            features.add(feature);

            final PredictionRequest requestObject = new PredictionRequest(features);

            final ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
            final String JSON = mapper.writeValueAsString(requestObject);

            Invocation.Builder request = predict.request(MediaType.APPLICATION_JSON_TYPE);

            if (USE_SELDON_TOKEN) {
                request = request.header("Authorization", "Bearer " + SELDON_TOKEN);
            }

            final String stringResponse = request.post(Entity.entity(JSON, MediaType.APPLICATION_JSON_TYPE), String.class);
            final PredictionResponse response = mapper.readValue(stringResponse, PredictionResponse.class);
            logger.debug(response.getData().getOutcomes().toString());

            logger.debug(stringResponse);

            List<Double> labelProbs = response.getData().getOutcomes().get(0);
            Map<String, Object> results = new HashMap<>();

            // Second label is probability of being a fraudulent transaction
            final double prob = labelProbs.get(1);
            results.put("probability", prob);

            logger.debug("Got parameters '" + sampleParam + "' and '" + sampleParamTwo + "'.");
            logger.debug("Will return '" + prob + "'");

            manager.completeWorkItem(workItem.getId(), results);
        } catch(Throwable cause) {
            handleException(cause);
        }
    }

    @Override
    public void abortWorkItem(WorkItem workItem,
                              WorkItemManager manager) {
        // stub
    }
}


