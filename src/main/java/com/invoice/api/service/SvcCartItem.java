package com.invoice.api.service;

import java.util.List;
import com.invoice.api.dto.in.DtoCartItemIn;
import com.invoice.api.dto.out.DtoCartItemDetailOut; 
import com.invoice.api.dto.out.DtoCartItemOut;
import com.invoice.commons.dto.ApiResponse;

public interface SvcCartItem {
    
    ApiResponse addItem(DtoCartItemIn in);
    
    List<DtoCartItemOut> getItems();
    
    List<DtoCartItemDetailOut> getItemsDetail();
    
    ApiResponse deleteItem(Integer id);

    ApiResponse clearCart();
}