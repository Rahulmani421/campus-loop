package com.campusloop.controller;

import com.campusloop.model.Category;
import com.campusloop.model.Product;
import com.campusloop.repository.ProductRepository;
import com.campusloop.repository.UserRepository;
import com.campusloop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Basic Stats
        long totalUsers = userRepository.count();
        long activeListings = productRepository.findBySoldFalseOrderByCreatedAtDesc().size();
        long itemsSold = productRepository.countBySoldTrue();
        
        // Advanced Analytics
        List<Product> allRegisteredProducts = productRepository.findAll();
        double totalVolume = allRegisteredProducts.stream()
                .mapToDouble(Product::getPrice)
                .sum();

        // Find Most Popular Category
        String topCategory = allRegisteredProducts.stream()
                .collect(Collectors.groupingBy(Product::getCategory, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> e.getKey().toString())
                .orElse("None");

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("activeListings", activeListings);
        model.addAttribute("itemsSold", itemsSold);
        model.addAttribute("totalVolume", totalVolume);
        model.addAttribute("topCategory", topCategory);
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("recentProducts", productRepository.findAll().stream()
                .sorted(Comparator.comparing(Product::getCreatedAt).reversed())
                .limit(5)
                .collect(Collectors.toList()));

        return "admin/dashboard";
    }
}
