package dev.ruivieira.ccfd.routes.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfiguration {

    @Value("${spring.application.name}")
    String appName;

    @Value("${env}")
    String environment;

    @Value("${instanceId}")
    String instanceId;

    @Autowired
    CamelContext camelContext;

    @Bean
    MeterRegistryCustomizer<PrometheusMeterRegistry> configureMetricsRegistry() {

        return registry -> {
            registry.config().commonTags("appName", appName, "env", environment, "instanceId", instanceId,"camelContext", camelContext.getName());
            Counter.builder("transaction.incoming")
                    .description("Incoming transactions")
                    .register(registry);
            Counter.builder("transaction.outgoing")
                    .tag("type", "fraud")
                    .description("Fraudulent transaction sent to KIE server")
                    .register(registry);
            Counter.builder("transaction.outgoing")
                    .tag("type", "standard")
                    .description("Fraudulent transaction sent to KIE server")
                    .register(registry);
            Counter.builder("notifications.outgoing")
                    .description("Outgoing customer notifications")
                    .register(registry);
            Counter.builder("notifications.incoming")
                    .tag("response", "approved")
                    .description("Customer responses approving transaction")
                    .register(registry);
            Counter.builder("notifications.incoming")
                    .tag("response", "non_approved")
                    .description("Customer responses disapproving transaction")
                    .register(registry);
        };

    }
}

