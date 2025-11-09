package com.invoice.api.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.invoice.api.dto.in.DtoCartItemIn;
import com.invoice.api.dto.out.DtoCartItemDetailOut; 
import com.invoice.api.dto.out.DtoCartItemOut;
import com.invoice.api.service.SvcCartItem;
import com.invoice.commons.dto.ApiResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/cart-item")
@Tag(name = "Cart", description = "API del Carrito de Compras")
public class CtrlCartItem {

    @Autowired
    private SvcCartItem svcCART;

    @PostMapping
    public ResponseEntity<ApiResponse> addItem(@Valid @RequestBody DtoCartItemIn in) {
        return new ResponseEntity<>(svcCART.addItem(in), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DtoCartItemOut>> getItems() {
        return new ResponseEntity<>(svcCART.getItems(), HttpStatus.OK);
    }
    
    @GetMapping("/detail")
    public ResponseEntity<List<DtoCartItemDetailOut>> getItemsDetail() {
        return new ResponseEntity<>(svcCART.getItemsDetail(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteItem(@PathVariable Integer id) {
        return new ResponseEntity<>(svcCART.deleteItem(id), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> clearCart() {
        return new ResponseEntity<>(svcCART.clearCart(), HttpStatus.OK);
    }
}