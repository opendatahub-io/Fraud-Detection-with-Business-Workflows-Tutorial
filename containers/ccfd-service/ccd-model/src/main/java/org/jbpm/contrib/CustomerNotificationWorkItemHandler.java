package org.jbpm.contrib;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jbpm.process.workitem.core.AbstractLogOrThrowWorkItemHandler;
import org.jbpm.process.workitem.core.util.RequiredParameterValidator;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.runtime.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CustomerNotificationWorkItemHandler extends AbstractLogOrThrowWorkItemHandler implements Cacheable {

    private static final Logger logger = LoggerFactory.getLogger(CustomerNotificationWorkItemHandler.class);
    private final KafkaProducer<Long, String> producer;
    private final String TOPIC;

    public CustomerNotificationWorkItemHandler() {
        TOPIC = System.getenv("CUSTOMER_NOTIFICATION_TOPIC");
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                System.getenv("BROKER_URL"));
        config.put(ProducerConfig.CLIENT_ID_CONFIG,
                "KafkaCustomerNotification");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                LongSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        this.producer = new KafkaProducer<>(config);
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
        try {
            RequiredParameterValidator.validate(this.getClass(),
                    workItem);
            Map<String, Object> results = new HashMap<>();
            logger.debug("Notifying customer by sending Kafka to notification from workItem id = " + workItem.getId());
            final Integer customerId = (Integer) workItem.getParameter("customer_id");
            final Double v3 = (Double) workItem.getParameter("v3");
            results.put("customer_id", customerId);
            results.put("v3", v3);
            results.put("process_id", workItem.getProcessInstanceId());
            ObjectMapper mapper = new ObjectMapper();
            producer.send(new ProducerRecord<>(TOPIC,
                    null,
                    mapper.writeValueAsString(results)));

            workItemManager.completeWorkItem(workItem.getId(), results);
        } catch (Exception e) {
            handleException(e);
        }


    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {

    }

    @Override
    public void close() {

    }
}
