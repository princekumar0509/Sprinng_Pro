package org.example.repository;

import org.example.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartItem, String> {
    // Get all items by userId
    List<CartItem> findByUserId(String id);

    // Get item by userId
    Optional<CartItem> findByUserIdAndItemId(String userId, String prodcutId);

    // Clear the entire cart
    void deleteByUserId(String userId);
}
