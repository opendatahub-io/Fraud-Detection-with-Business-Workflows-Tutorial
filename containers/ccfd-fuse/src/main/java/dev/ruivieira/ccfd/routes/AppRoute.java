package dev.ruivieira.ccfd.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dev.ruivieira.ccfd.routes.messages.MessageParser;
import dev.ruivieira.ccfd.routes.messages.NotificationResponse;
import dev.ruivieira.ccfd.routes.messages.v1.PredictionRequest;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AppRoute extends RouteBuilder {


    private static final Logger logger = LoggerFactory.getLogger(AppRoute.class);
    private final ObjectMapper requestMapper = new ObjectMapper();
    private boolean USE_SELDON_TOKEN = false;
    private String SELDON_TOKEN;

    private static final String SELDON_ENDPOINT_KEY = "SELDON_ENDPOINT";
    private static final String SELDON_ENDPOINT_DEFAULT = "predict";
    private String SELDON_ENDPOINT;

    public AppRoute() {
    }

    public void configure() {

        requestMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);

        final String BROKER_URL = System.getenv("BROKER_URL");
        final String KAFKA_TOPIC = System.getenv("KAFKA_TOPIC");
        final String KIE_SERVER_URL = System.getenv("KIE_SERVER_URL");
        final String SELDON_URL = System.getenv("SELDON_URL");
        SELDON_TOKEN = System.getenv("SELDON_TOKEN");

        if (BROKER_URL == null) {
            final String message = "No Kafka broker provided";
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        if (KAFKA_TOPIC == null) {
            final String message = "No Kafka topic provided";
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        if (KIE_SERVER_URL == null) {
            final String message = "No KIE server provided";
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        if (SELDON_URL == null) {
            final String message = "No Seldon server provided";
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        SELDON_ENDPOINT = System.getenv(SELDON_ENDPOINT_KEY);

        if (SELDON_ENDPOINT == null) {
            logger.debug("Using default Seldon endpoint /predict");
            SELDON_ENDPOINT = SELDON_ENDPOINT_DEFAULT;
        } else {
            logger.debug("Using custom Seldon endpoint " + SELDON_ENDPOINT);
        }

        // TODO: write guards
        final String CUSTOMER_NOTIFICATION_TOPIC = System.getenv("CUSTOMER_NOTIFICATION_TOPIC");
        final String CUSTOMER_RESPONSE_TOPIC = System.getenv("CUSTOMER_RESPONSE_TOPIC");

        USE_SELDON_TOKEN = SELDON_TOKEN != null;

        final AggregationStrategy seldonStrategy = new SeldonAggregationStrategy();

        from("kafka:" + KAFKA_TOPIC + "?brokers=" + BROKER_URL).routeId("mainRoute")
                .log("incoming payload: ${body}")
                .to("micrometer:counter:transaction.incoming?increment=1")
                .process(exchange -> {
                    // deserialise Kafka message
                    final List<Double> feature = new ArrayList<>();
                    final String payload = exchange.getIn().getBody().toString();
                    final List<String> kafkaFeatures;

                    final int[] indices = {4, 5, 11, 12, 13, 15, 18, 30};
                    kafkaFeatures = MessageParser.parseV1(payload);
                    // extract the features of interest
                    for (int index : indices) {
                        feature.add(Double.parseDouble(kafkaFeatures.get(index-1)));
                    }
                    Integer amountIndex = 29;
                    
                    //exchange.getOut().setHeader("amount", Double.parseDouble(kafkaFeatures.get(amountIndex)));
                    exchange.getMessage().setHeader("amount", Double.parseDouble(kafkaFeatures.get(amountIndex)));

                    String outgoingPayload;             
                    outgoingPayload = "{\"strData\":\"";

                    outgoingPayload += feature.stream()
                            .map(Object::toString)
                            .collect(Collectors.joining(","));
                    outgoingPayload += "\"}";
                    
                    exchange.getMessage().setBody(outgoingPayload);
                    if (USE_SELDON_TOKEN) {
                        exchange.getMessage().setHeader("Authorization", "Bearer " + SELDON_TOKEN);
                    }

                })
                .log("outgoing payload: ${body}")

                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .enrich(SELDON_URL + "/" + SELDON_ENDPOINT, seldonStrategy)
                .log("enriched: ${body}")
                .choice()
                .when(header("fraudulent").isEqualTo(true))
                .marshal(new JacksonDataFormat())
                .to("micrometer:counter:transaction.outgoing?increment=1&tags=type=fraud")
                .to(KIE_SERVER_URL + "/rest/server/containers/ccd-fraud-kjar-1_1-CCFD/processes/ccd-fraud-kjar.CCDProcess/instances")
                .otherwise()
                .marshal(new JacksonDataFormat())
                .to("micrometer:counter:transaction.outgoing?increment=1&tags=type=standard")
                .to(KIE_SERVER_URL + "/rest/server/containers/ccd-fraud-kjar-1_1-CCFD/processes/ccd-fraud-kjar.CCDProcess/instances");

        from("kafka:" + CUSTOMER_NOTIFICATION_TOPIC + "?brokers=" + BROKER_URL).routeId("customerIncoming")
                .log("${body}")
                .to("micrometer:counter:notifications.outgoing?increment=1");

        from("kafka:" + CUSTOMER_RESPONSE_TOPIC + "?brokers=" + BROKER_URL).routeId("customerResponse")
                .process(exchange -> {
                    final String payload = exchange.getIn().getBody(String.class);
                    ObjectMapper mapper = new ObjectMapper();
                    NotificationResponse response = mapper.readValue(payload, NotificationResponse.class);
                    exchange.getMessage().setHeader("processId", response.responseId);
                    exchange.getMessage().setBody(response.response);
                    exchange.getMessage().setHeader("response", response.response ? "approved" : "non_approved");
                })
                .marshal(new JacksonDataFormat())
                .log("${body}")
                .to("micrometer:counter:notifications.incoming?increment=1&tags=response=${header.response}")
                .toD(KIE_SERVER_URL + "/rest/server/containers/ccd-fraud-kjar-1_1-CCFD/processes/instances/${header.processId}/signal/customerAcknowledgement");
    }
}
