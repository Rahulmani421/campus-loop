package com.campusloop.repository;

import com.campusloop.model.Category;
import com.campusloop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findBySoldFalseOrderByCreatedAtDesc();
    
    @Query("SELECT p FROM Product p WHERE (LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))) AND p.category = :category AND p.sold = false")
    List<Product> searchProducts(String query, Category category);
    
    long countBySoldTrue();
    List<Product> findBySoldTrue();
}
