package dev.ruivieira.ccfd.notification.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class NotificationRequest {

    @JsonProperty("customer_id")
    public int customerId;

    public double v3;

    @JsonProperty("process_id")
    public long processId;
}
