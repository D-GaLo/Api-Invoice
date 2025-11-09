package com.invoice.api.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.invoice.api.entity.CartItem;

@Repository
public interface RepoCartItem extends JpaRepository<CartItem, Integer> {

    List<CartItem> findByUserIdAndStatus(Integer userId, Integer status);

    Optional<CartItem> findByUserIdAndGtinAndStatus(Integer userId, String gtin, Integer status);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem c WHERE c.userId = :user_id AND c.status = 1")
    void deleteByUserIdAndStatus(@Param("user_id") Integer userId);
}