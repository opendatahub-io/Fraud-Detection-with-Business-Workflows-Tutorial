package dev.ruivieira.ccfd.notification.streams;

import dev.ruivieira.ccfd.notification.model.*;
import io.quarkus.kafka.client.serialization.JsonbSerde;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.ProcessorContext;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Random;

@ApplicationScoped
public class TopologyProducer {

    static final String CUSTOMER_OUTGOING_TOPIC = "ccd-customer-outgoing";
    static final String CUSTOMER_RESPONSE_TOPIC = "ccd-customer-response";

    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        JsonbSerde<NotificationRequest> notificationSerde = new JsonbSerde<>(
                NotificationRequest.class);

        final Serializer<NotificationRequest> notificationRequestSerializer = new NotificationRequestSerializer();
        final Deserializer<NotificationRequest> notificationRequestDeserializer = new NotificationRequestDeserializer();

        final Serde<NotificationRequest> notificationRequestSerde = Serdes.serdeFrom(notificationRequestSerializer, notificationRequestDeserializer);

        final Serializer<NotificationResponse> notificationResponseSerializer = new NotificationResponseSerializer();
        final Deserializer<NotificationResponse> notificationResponseDeserializer = new NotificationResponseDeserializer();

        final Serde<NotificationResponse> notificationResponseSerde = Serdes.serdeFrom(notificationResponseSerializer, notificationResponseDeserializer);

        ValueTransformerSupplier<NotificationRequest, NotificationResponse> valueTransformerSupplier = new ValueTransformerSupplier<NotificationRequest, NotificationResponse>() {

            @Override
            public ValueTransformer<NotificationRequest, NotificationResponse> get() {
                return new ValueTransformer<NotificationRequest, NotificationResponse>() {
                    @Override
                    public void init(ProcessorContext processorContext) {

                    }

                    @Override
                    public NotificationResponse transform(NotificationRequest notificationRequest) {
                        NotificationResponse response = new NotificationResponse();
                        response.customerId = notificationRequest.customerId;
                        // 50% percent chance customer acknowledges transaction
                        response.response = Math.random() < 0.5;
                        response.responseId = notificationRequest.processId;
                        System.out.println("Manufacturing a response of true for customer " + response.customerId);
                        return response;
                    }

                    @Override
                    public void close() {

                    }
                };
            }
        };

        KStream<String, NotificationResponse> filteredStream = builder.stream(
                CUSTOMER_OUTGOING_TOPIC, /* input topic */
                Consumed.with(
                        Serdes.String(), /* key serde */
                        notificationRequestSerde   /* value serde */
                ))
                // we will say that half the notifications do not get a response from the client
                .filter((key, value) -> {
//                    System.out.println("Notification request for - key = " + key + ", value = " + value.customerId);
                    return Math.random() < 0.5;
                })
                .transformValues(valueTransformerSupplier);

                filteredStream.to(CUSTOMER_RESPONSE_TOPIC, Produced.with(Serdes.String(), notificationResponseSerde));
        return builder.build();
    }
}