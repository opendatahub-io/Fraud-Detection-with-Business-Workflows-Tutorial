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

import org.kie.internal.task.api.prediction.PredictionService;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;


public class SeldonPredictionServiceRegistry {
    private static final ServiceLoader<PredictionService> foundServices = ServiceLoader.load(PredictionService.class, SeldonPredictionServiceRegistry.class.getClassLoader());
    private String selectedService = System.getProperty("org.jbpm.task.prediction.service", SeldonPredictionService.IDENTIFIER);
    private Map<String, PredictionService> predictionServices = new HashMap<>();

    private SeldonPredictionServiceRegistry() {

        foundServices
                .forEach(strategy -> predictionServices.put(strategy.getIdentifier(), strategy));
    }

    public static SeldonPredictionServiceRegistry get() {
        return Holder.INSTANCE;
    }

    public PredictionService getService() {
        PredictionService predictionService = predictionServices.get(selectedService);
        if (predictionService == null) {
            throw new IllegalArgumentException("No prediction service was found with id " + selectedService);
        }

        return predictionService;
    }

    public synchronized void addStrategy(SeldonPredictionService predictionService) {
        this.predictionServices.put(predictionService.getIdentifier(), predictionService);

    }

    private static class Holder {
        static final SeldonPredictionServiceRegistry INSTANCE = new SeldonPredictionServiceRegistry();
    }
}
