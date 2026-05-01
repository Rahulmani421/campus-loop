package com.campusloop.service;

import com.campusloop.model.Category;
import com.campusloop.model.Product;
import com.campusloop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllActiveProducts() {
        return productRepository.findBySoldFalseOrderByCreatedAtDesc();
    }

    public List<Product> searchProducts(String query, Category category) {
        return productRepository.searchProducts(query, category);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }
}
