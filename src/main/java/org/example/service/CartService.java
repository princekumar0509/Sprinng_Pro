package org.example.service;

import org.example.model.CartItem;
import org.example.model.Product;
import org.example.repository.CartRepository;
import org.example.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    // POST /api/cart/add
    public CartItem addItemToCart(String userId, String productId, Integer quantity){
        // Validate product exists
        productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> existingItem = cartRepository.findByUserIdAndProductId(userId, productId);

        if(existingItem.isPresent()){
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            return cartRepository.save(item);
        }

        CartItem cartItem = new CartItem();
        cartItem.setUserId(userId);
        cartItem.setProductId(productId);
        cartItem.setQuantity(quantity);
        return cartRepository.save(cartItem);
    }


    // GET /api/cart/
    public List<Map<String, Object>> getUserCart(String userId){
        List<CartItem> cartItems = cartRepository.findByUserId(userId);
        List<Map<String, Object>> response = new ArrayList<>();

        for (CartItem item : cartItems) {

            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            Map<String, Object> cartItemResponse = new HashMap<>();
            cartItemResponse.put("id", item.getId());
            cartItemResponse.put("productId", item.getProductId());
            cartItemResponse.put("quantity", item.getQuantity());

            Map<String, Object> productInfo = new HashMap<>();
            productInfo.put("id", product.getId());
            productInfo.put("name", product.getName());
            productInfo.put("price", product.getPrice());

            cartItemResponse.put("product", productInfo);

            response.add(cartItemResponse);
        }

        return response;
    }

    // DELETE /api/cart/clear
    public void clearCart(String userId){
        cartRepository.deleteByUserId(userId);
    }
}
