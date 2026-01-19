package org.example.controller;

import org.example.dto.AddToCartRequest;
import org.example.model.CartItem;
import org.example.service.CartService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public CartItem addToCart(@RequestBody AddToCartRequest request) {
        return cartService.addItemToCart(request.getUserId(), request.getProductId(), request.getQuantity());
    }

    @GetMapping("/{userId}")
    public List<Map<String, Object>> getCart(@PathVariable String userId) {
        return cartService.getUserCart(userId);
    }

    @DeleteMapping("/{userId}/clear")
    public Map<String, String> clearCart(@PathVariable String userId) {
        cartService.clearCart(userId);
        return Map.of("message", "Cart cleared successfully");
    }
}
