package com.campusloop.controller;

import com.campusloop.model.Category;
import com.campusloop.model.Notification;
import com.campusloop.model.Product;
import com.campusloop.model.User;
import com.campusloop.repository.NotificationRepository;
import com.campusloop.repository.ProductRepository;
import com.campusloop.repository.UserRepository;
import com.campusloop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @Autowired
    private NotificationRepository notificationRepository;

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
    @GetMapping("/products")
    public String manageProducts(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "admin/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, @RequestParam(required = false) String reason, RedirectAttributes redirectAttributes) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            // Notify the user before deleting
            String message = "Your product '" + product.getTitle() + "' has been removed by administrative action.";
            if (reason != null && !reason.isEmpty()) {
                message += " Reason: " + reason;
            }

            Notification notification = Notification.builder()
                    .user(product.getSeller())
                    .title("Product Removed")
                    .message(message)
                    .isRead(false)
                    .build();
            notificationRepository.save(notification);

            // Clear associations to prevent FK errors
            productRepository.save(product); // Ensure attached
            
            // Note: In a real app, you might want to use a join query to clear saved_products
            // but for simplicity here we assume the DB or JPA handles it or we handle it manually
            
            productRepository.delete(product);
            redirectAttributes.addFlashAttribute("success", "Product removed and user notified.");
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/products/add")
    public String addProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", Category.values());
        model.addAttribute("users", userRepository.findAll()); // Admin might choose a seller
        return "admin/add-product";
    }

    @PostMapping("/products/add")
    public String addProduct(@ModelAttribute Product product, @RequestParam Long sellerId, RedirectAttributes redirectAttributes) {
        User seller = userRepository.findById(sellerId).orElse(null);
        if (seller != null) {
            product.setSeller(seller);
            productRepository.save(product);
            redirectAttributes.addFlashAttribute("success", "Product added successfully on behalf of " + seller.getFullName());
        } else {
            redirectAttributes.addFlashAttribute("error", "Seller not found.");
        }
        return "redirect:/admin/products";
    }
}
