package dev.ruivieira.service.prometheus;

import io.prometheus.client.Histogram;
import org.kie.api.event.process.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MyProcessEventListener implements org.kie.api.event.process.ProcessEventListener {

    private static final Histogram investigationAmount = Histogram.build()
            .name("fraud_investigation_amount")
            .help("Amount for a transaction that will be investigated as fraud")
            .buckets(100, 1000, 5000)
            .register();
    private static final Histogram lowAmount = Histogram.build()
            .name("fraud_approved_low_amount")
            .help("Amount for a transaction not acknowledged by customer, but approved by DMN (mainly due to low amount)")
            .buckets(100, 1000, 5000)
            .register();
    private static final Histogram approvedAmount = Histogram.build()
            .name("fraud_approved_amount")
            .help("Amount for a transaction that was approved by the customer")
            .buckets(100, 1000, 5000)
            .register();
    private static final Histogram rejectedAmount = Histogram.build()
            .name("fraud_rejected_amount")
            .help("Amount for a transaction that was rejected by the customer")
            .buckets(100, 1000, 5000)
            .register();

    private static final Logger logger = LoggerFactory.getLogger(MyProcessEventListener.class);

    @Override
    public void beforeProcessStarted(ProcessStartedEvent processStartedEvent) {

    }

    @Override
    public void afterProcessStarted(ProcessStartedEvent processStartedEvent) {

    }

    @Override
    public void beforeProcessCompleted(ProcessCompletedEvent processCompletedEvent) {

    }

    @Override
    public void afterProcessCompleted(ProcessCompletedEvent processCompletedEvent) {

    }

    @Override
    public void beforeNodeTriggered(ProcessNodeTriggeredEvent processNodeTriggeredEvent) {

    }

    @Override
    public void afterNodeTriggered(ProcessNodeTriggeredEvent node) {
        String name = node.getNodeInstance().getNode().getName();

        if (name != null && name.equals("Start investigation")) {
            Double amount = (Double) node.getNodeInstance().getProcessInstance().getVariable("amount");
            investigationAmount.observe(amount);
        }
        if (name != null && name.equals("Approved by customer")) {
            Double amount = (Double) node.getNodeInstance().getProcessInstance().getVariable("amount");
            approvedAmount.observe(amount);
        }
        if (name != null && name.equals("Cancel transaction")) {
            Double amount = (Double) node.getNodeInstance().getProcessInstance().getVariable("amount");
            rejectedAmount.observe(amount);
        }
        if (name != null && name.equals("Approve transaction")) {
            Double amount = (Double) node.getNodeInstance().getProcessInstance().getVariable("amount");
            lowAmount.observe(amount);
        }
    }

    @Override
    public void beforeNodeLeft(ProcessNodeLeftEvent processNodeLeftEvent) {

    }

    @Override
    public void afterNodeLeft(ProcessNodeLeftEvent processNodeLeftEvent) {

    }

    @Override
    public void beforeVariableChanged(ProcessVariableChangedEvent processVariableChangedEvent) {

    }

    @Override
    public void afterVariableChanged(ProcessVariableChangedEvent processVariableChangedEvent) {

    }
}
