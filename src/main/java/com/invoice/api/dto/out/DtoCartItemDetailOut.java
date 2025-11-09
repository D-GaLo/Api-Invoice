package com.invoice.api.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class DtoCartItemDetailOut {

    @JsonProperty("cart_item_id")
    private Integer cartItemId;
    
    private String gtin;
    private Integer quantity;

    @JsonProperty("product_name")
    private String productName;
    
    @JsonProperty("unit_price")
    private Float unitPrice;
    
    private String description;
    private Integer stock;
    private String category;
    private List<Map<String, String>> images;
    
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
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Integer getStock() {
        return stock;
    }
    public void setStock(Integer stock) {
        this.stock = stock;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public List<Map<String, String>> getImages() {
        return images;
    }
    public void setImages(List<Map<String, String>> images) {
        this.images = images;
    }
}