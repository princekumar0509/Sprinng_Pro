package org.example.service;

import org.example.model.Product;
import org.example.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // POST /api/products
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    // GET /api/products
    public List<Map<String, Object>> getAllProducts() {

        List<Product> products = productRepository.findAll();
        List<Map<String, Object>> response = new ArrayList<>();

        for (Product product : products) {
            Map<String, Object> productResponse = new HashMap<>();
            productResponse.put("id", product.getId());
            productResponse.put("name", product.getName());
            productResponse.put("price", product.getPrice());
            productResponse.put("stock", product.getStock());

            response.add(productResponse);
        }

        return response;
    }
}