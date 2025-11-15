package com.invoice.api.service;

import java.time.LocalDate; 
import java.util.ArrayList; 
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; 
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpEntity; 
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod; 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException; 
import org.springframework.web.client.RestTemplate; 

import com.invoice.api.dto.ApiResponse;
import com.invoice.api.dto.DtoInvoiceList;
import com.invoice.api.dto.in.DtoProductIn; 
import com.invoice.api.dto.out.DtoProductResponse; 
import com.invoice.api.entity.CartItem; 
import com.invoice.api.entity.Invoice;
import com.invoice.api.entity.InvoiceItem; 
import com.invoice.api.repository.RepoCartItem; 
import com.invoice.api.repository.RepoInvoice;
import com.invoice.commons.mapper.MapperInvoice;
import com.invoice.commons.util.JwtDecoder;
import com.invoice.exception.ApiException;
import com.invoice.exception.DBAccessException;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class SvcInvoiceImp implements SvcInvoice {
	
	@Autowired
    private RepoInvoice repo;

	// Inicio nuevo
	@Autowired
	private RepoCartItem repoCartItem;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private HttpServletRequest request;
	
	@Value("${api.product.url}")
	private String productApiUrl;

	// Fin nuevo
	
	@Autowired
	private JwtDecoder jwtDecoder;
	
	@Autowired
	MapperInvoice mapper;

	@Override
	public List<DtoInvoiceList> findAll() {
		try {
			if(jwtDecoder.isAdmin()) {
				return mapper.toDtoList(repo.findAll());
			}else {
				Integer user_id = jwtDecoder.getUserId();
				return mapper.toDtoList(repo.findAllByUserId(user_id));
			}
		}catch (DataAccessException e) {
	        throw new DBAccessException();
	    }
	}

	@Override
	public Invoice findById(Integer id) {
		try {
			Invoice invoice = repo.findById(id).get();
			if(!jwtDecoder.isAdmin()) {
				Integer user_id = jwtDecoder.getUserId();
				if(invoice.getUser_id() != user_id) {
					throw new ApiException(HttpStatus.FORBIDDEN, "El token no es válido para consultar esta factura");
				}
			}
			return invoice;
		}catch (DataAccessException e) {
	        throw new DBAccessException();
	    }catch (NoSuchElementException e) {
			throw new ApiException(HttpStatus.NOT_FOUND, "El id de la factura no existe");
	    }
	}

	@Override
	@Transactional
	public ApiResponse create() {
		Integer userId = jwtDecoder.getUserId();
		
		List<CartItem> items = repoCartItem.findByUserIdAndStatus(userId, 1);
		if (items.isEmpty()) {
			throw new ApiException(HttpStatus.NOT_FOUND, "El carrito está vacío");
		}
		
		List<DtoProductResponse> productDetails = new ArrayList<>();
		List<InvoiceItem> invoiceItems = new ArrayList<>();
		
		Double total = 0.0;
		Double taxes = 0.0;
		Double subtotal = 0.0;
		
		try {
			for (CartItem cartItem : items) {

				DtoProductResponse product = getProductFromApi(cartItem.getGtin());
				
				if (product.getStock() < cartItem.getQuantity()) {
					throw new ApiException(HttpStatus.CONFLICT, "Stock insuficiente para el producto: " + product.getProduct());
				}
				productDetails.add(product);
				
				Double itemTotal = (double) (product.getPrice() * cartItem.getQuantity());
				Double itemTaxes = itemTotal * 0.16;
				Double itemSubtotal = itemTotal - itemTaxes;
				
				total += itemTotal;
				taxes += itemTaxes;
				subtotal += itemSubtotal;
				
				InvoiceItem invoiceItem = new InvoiceItem();
				invoiceItem.setGtin(cartItem.getGtin());
				invoiceItem.setQuantity(cartItem.getQuantity());
				invoiceItem.setUnit_price(Double.valueOf(product.getPrice()));
				invoiceItem.setTotal(itemTotal);
				invoiceItem.setTaxes(itemTaxes);
				invoiceItem.setSubtotal(itemSubtotal);
				invoiceItem.setStatus(1); // 1 = Activo
				invoiceItems.add(invoiceItem);
			}
			
			Invoice invoice = new Invoice();
			invoice.setUser_id(userId);
			invoice.setCreated_at(LocalDate.now());
			invoice.setTotal(total);
			invoice.setTaxes(taxes);
			invoice.setSubtotal(subtotal);
			invoice.setStatus(1); // 1 = Pagada
			invoice.setItems(invoiceItems); 
			
			repo.save(invoice); 
			
			
			for (int i = 0; i < items.size(); i++) {
				CartItem cartItem = items.get(i);
				DtoProductResponse product = productDetails.get(i);
				Integer newStock = product.getStock() - cartItem.getQuantity();
				
				updateProductStockInApi(product, newStock);
			}
			
			repoCartItem.deleteByUserIdAndStatus(userId);
			
			return new ApiResponse("La factura ha sido registrada");
			
		} catch (DataAccessException e) {
	        throw new DBAccessException(e);
		} catch (Exception e) {
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al finalizar la compra: " + e.getMessage());
		}
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
				url, HttpMethod.GET, entity, DtoProductResponse.class
			);
			if (response.getBody() == null) {
				 throw new ApiException(HttpStatus.NOT_FOUND, "El producto (gtin: " + gtin + ") no existe");
			}
			return response.getBody();
		} catch (HttpClientErrorException.NotFound e) {
			throw new ApiException(HttpStatus.NOT_FOUND, "El producto (gtin: " + gtin + ") no existe");
		} catch (Exception e) {
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al consultar el servicio de productos: " + e.getMessage());
		}
	}
    

	private void updateProductStockInApi(DtoProductResponse product, Integer newStock) {
		final String authHeader = request.getHeader("Authorization");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", authHeader);
		
		DtoProductIn productStockUpdate = new DtoProductIn();
		productStockUpdate.setGtin(product.getGtin());
		productStockUpdate.setProduct(product.getProduct());
		productStockUpdate.setDescription(product.getDescription());
		productStockUpdate.setPrice(product.getPrice());
		productStockUpdate.setStock(newStock);
		productStockUpdate.setCategory_id(product.getCategory_id());
		
		HttpEntity<DtoProductIn> entity = new HttpEntity<>(productStockUpdate, headers);
		
		try {
			String url = productApiUrl + "/product/" + product.getProductId();
			restTemplate.exchange(
				url, HttpMethod.PUT, entity, ApiResponse.class
			);
		} catch (Exception e) {
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar el stock del producto: " + product.getProduct() + ". Contacte a soporte.");
		}
	}
}
