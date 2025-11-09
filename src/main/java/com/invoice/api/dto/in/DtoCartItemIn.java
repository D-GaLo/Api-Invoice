package com.invoice.api.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class DtoCartItemIn {

    @JsonProperty("gtin")
    @NotNull(message = "El gtin es obligatorio")
    @Pattern(regexp = "^\\+?\\d{13}$", message = "El gtin tiene un formato inválido")
    private String gtin;

    @JsonProperty("quantity")
    @NotNull(message = "La quantity es obligatoria")
    @Min(value = 1, message = "La cantidad mínima debe ser 1")
    private Integer quantity;

    public String getGtin() {
        return gtin;
    }
    public void setGtin(String gtin) {
        this.gtin = gtin;
    }
    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}