package dev.ruivieira.ccfd.routes.model;

public class Classification {

    public boolean isFraudulent() {
        return fraudulent;
    }

    public void setFraudulent(boolean fraudulent) {
        this.fraudulent = fraudulent;
    }

    private boolean fraudulent;
}
