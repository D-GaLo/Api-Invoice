package com.invoice.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "invoice_item")
public class InvoiceItem {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_item_id") 
	private Integer invoice_item_id; 
	
	@JsonIgnore
    @Column(name = "invoice_id") 
	private Integer invoice_id; 
	
    @Column(name = "gtin")
	private String gtin;

    @Column(name = "quantity") 
	private Integer quantity;

    @Column(name = "unit_price")
	private Double unit_price;

    @Column(name = "subtotal")
	private Double subtotal;

    @Column(name = "taxes")
	private Double taxes;

    @Column(name = "total")
	private Double total;
    
    @Column(name = "status")
    private Integer status;
	
	// Getters y Setters
	public Integer getInvoice_item_id() {
		return invoice_item_id;
	}
	public void setInvoice_item_id(Integer invoice_item_id) {
		this.invoice_item_id = invoice_item_id;
	}
	public Integer getInvoice_id() {
		return invoice_id;
	}
	public void setInvoice_id(Integer invoice_id) {
		this.invoice_id = invoice_id;
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
	public Double getUnit_price() {
		return unit_price;
	}
	public void setUnit_price(Double unit_price) {
		this.unit_price = unit_price;
	}
	public Double getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(Double subtotal) {
		this.subtotal = subtotal;
	}
	public Double getTaxes() {
		return taxes;
	}
	public void setTaxes(Double taxes) {
		this.taxes = taxes;
	}
	public Double getTotal() {
		return total;
	}
	public void setTotal(Double total) {
		this.total = total;
	}
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
}