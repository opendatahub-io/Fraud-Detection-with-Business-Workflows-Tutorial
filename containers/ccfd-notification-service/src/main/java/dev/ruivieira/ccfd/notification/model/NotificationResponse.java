package dev.ruivieira.ccfd.notification.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class NotificationResponse {

    public int customerId;
    public long responseId;
    public boolean response;
}
