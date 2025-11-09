package com.invoice.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import com.invoice.api.dto.in.DtoCartItemIn;
import com.invoice.api.dto.out.DtoCartItemDetailOut;
import com.invoice.api.dto.out.DtoCartItemOut;
import com.invoice.api.dto.out.DtoProductResponse;
import com.invoice.api.entity.CartItem;
import com.invoice.api.repository.RepoCartItem;
import com.invoice.commons.dto.ApiResponse;
import com.invoice.commons.util.JwtDecoder;
import com.invoice.exception.ApiException;

@Service
public class SvcCartItemImp implements SvcCartItem {

    @Autowired
    private RepoCartItem repoCartItem;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HttpServletRequest request; 

    @Value("${api.product.url}")
    private String productApiUrl;

    @Override
    public ApiResponse addItem(DtoCartItemIn in) {
        Integer userId = jwtDecoder.getUserId();
        DtoProductResponse product = getProductFromApi(in.getGtin());
        
        if (product.getStatus() == 0) {
            throw new ApiException(HttpStatus.NOT_FOUND, "El producto no está disponible");
        }

        Optional<CartItem> existingItemOpt = repoCartItem.findByUserIdAndGtinAndStatus(userId, in.getGtin(), 1);

        int newQuantity = in.getQuantity();

        if (existingItemOpt.isPresent()) {
            CartItem item = existingItemOpt.get();
            newQuantity = item.getQuantity() + in.getQuantity(); 
            
            if (product.getStock() < newQuantity) {
                 throw new ApiException(HttpStatus.CONFLICT, "Stock insuficiente. Solo quedan " + product.getStock() + " unidades.");
            }
            item.setQuantity(newQuantity);
            repoCartItem.save(item);
            return new ApiResponse("Producto actualizado en el carrito");
        } else {
            if (product.getStock() < newQuantity) {
                 throw new ApiException(HttpStatus.CONFLICT, "Stock insuficiente. Solo quedan " + product.getStock() + " unidades.");
            }
            
            CartItem newItem = new CartItem();
            newItem.setUserId(userId);
            newItem.setGtin(in.getGtin());
            newItem.setQuantity(newQuantity);
            newItem.setStatus(1); 
            repoCartItem.save(newItem);
            return new ApiResponse("Producto agregado al carrito");
        }
    }

    @Override
    public List<DtoCartItemOut> getItems() {
        Integer userId = jwtDecoder.getUserId();
        List<CartItem> items = repoCartItem.findByUserIdAndStatus(userId, 1);
        List<DtoCartItemOut> outList = new ArrayList<>();

        for (CartItem item : items) {
            DtoProductResponse product = getProductFromApi(item.getGtin());
            
            DtoCartItemOut dto = new DtoCartItemOut();
            dto.setCartItemId(item.getCartItemId());
            dto.setGtin(item.getGtin());
            dto.setQuantity(item.getQuantity());
            
            dto.setProductName(product.getProduct());
            dto.setUnitPrice(product.getPrice());
            
            outList.add(dto);
        }
        return outList;
    }

    @Override
    public List<DtoCartItemDetailOut> getItemsDetail() {
        Integer userId = jwtDecoder.getUserId();
        List<CartItem> items = repoCartItem.findByUserIdAndStatus(userId, 1);
        List<DtoCartItemDetailOut> outList = new ArrayList<>();

        for (CartItem item : items) {
            DtoProductResponse product = getProductFromApi(item.getGtin());
            
            DtoCartItemDetailOut dto = new DtoCartItemDetailOut();
            // Datos del carrito
            dto.setCartItemId(item.getCartItemId());
            dto.setGtin(item.getGtin());
            dto.setQuantity(item.getQuantity());
            
            // Datos básicos del producto
            dto.setProductName(product.getProduct());
            dto.setUnitPrice(product.getPrice());
            
            // Datos de detalle del producto
            dto.setDescription(product.getDescription());
            dto.setStock(product.getStock());
            dto.setCategory(product.getCategory());
            dto.setImages(product.getImages());
            
            outList.add(dto);
        }
        return outList;
    }

    @Override
    public ApiResponse deleteItem(Integer id) {
        Integer userId = jwtDecoder.getUserId();
        
        Optional<CartItem> itemOpt = repoCartItem.findById(id);
        if (itemOpt.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "El producto no existe en el carrito");
        }
        
        CartItem item = itemOpt.get();
        
        if (!item.getUserId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "No tienes permiso para eliminar este producto");
        }
        
        repoCartItem.delete(item);
        return new ApiResponse("Producto eliminado del carrito");
    }

    @Override
    public ApiResponse clearCart() {
        Integer userId = jwtDecoder.getUserId();
        repoCartItem.deleteByUserIdAndStatus(userId);
        return new ApiResponse("Carrito vaciado");
    }

    private DtoProductResponse getProductFromApi(String gtin) {
        
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Falta el token de autorización");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader); 

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            String url = productApiUrl + "/product/gtin/" + gtin;
            
            ResponseEntity<DtoProductResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity, 
                DtoProductResponse.class
            );
            
            if (response.getBody() == null) {
                 throw new ApiException(HttpStatus.NOT_FOUND, "El producto (gtin: " + gtin + ") no existe");
            }
            return response.getBody();
            
        } catch (HttpClientErrorException.NotFound e) {
            throw new ApiException(HttpStatus.NOT_FOUND, "El producto (gtin: " + gtin + ") no existe");
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Token no válido para el servicio de productos");
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al consultar el servicio de productos: "  + e.getMessage());
        }
    }
}