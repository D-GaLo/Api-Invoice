package com.invoice.api.dto.in;

// Este DTO es un espejo del DtoProductIn en api-product
// Se usa para enviar la actualización de stock
public class DtoProductIn {

	private String gtin;
	private String product;
	private String description;
	private Float price;
	private Integer stock;
	private Integer category_id;

	// Getters y Setters
	public String getGtin() {
		return gtin;
	}
	public void setGtin(String gtin) {
		this.gtin = gtin;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Float getPrice() {
		return price;
	}
	public void setPrice(Float price) {
		this.price = price;
	}
	public Integer getStock() {
		return stock;
	}
	public void setStock(Integer stock) {
		this.stock = stock;
	}
	public Integer getCategory_id() {
		return category_id;
	}
	public void setCategory_id(Integer category_id) {
		this.category_id = category_id;
	}
}