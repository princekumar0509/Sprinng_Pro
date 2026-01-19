package org.example.repository;

import org.example.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {
    // Add order from cart
    List<Order> findByUserId(String userId);
}
