package com.invoice.api.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DtoCartItemOut {

    @JsonProperty("cart_item_id")
    private Integer cartItemId;
    
    private String gtin;
    private Integer quantity;

    @JsonProperty("product_name")
    private String productName;
    
    @JsonProperty("unit_price")
    private Float unitPrice;
    
    public Integer getCartItemId() {
        return cartItemId;
    }
    public void setCartItemId(Integer cartItemId) {
        this.cartItemId = cartItemId;
    }
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
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public Float getUnitPrice() {
        return unitPrice;
    }
    public void setUnitPrice(Float unitPrice) {
        this.unitPrice = unitPrice;
    }
}