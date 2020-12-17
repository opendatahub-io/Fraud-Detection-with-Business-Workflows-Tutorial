package dev.ruivieira.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class Transaction {

    @JsonProperty(value = "id", required = true)
    private Integer id;

    @JsonProperty(value = "amount", required = true)
    private Double amount;

    public Integer getId() {
        return id;
    }

    public Double getAmount() {
        return amount;
    }
}