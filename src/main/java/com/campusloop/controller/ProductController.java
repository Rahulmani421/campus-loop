package com.campusloop.controller;

import com.campusloop.model.Category;
import com.campusloop.model.Condition;
import com.campusloop.model.Product;
import com.campusloop.model.User;
import com.campusloop.service.ProductService;
import com.campusloop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @GetMapping("/post")
    public String postItemForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", Category.values());
        model.addAttribute("conditions", Condition.values());
        return "post-item";
    }

    @PostMapping("/post")
    public String postItem(@ModelAttribute Product product, Principal principal) {
        if (principal != null) {
            User seller = userService.findByUsername(principal.getName()).orElse(null);
            product.setSeller(seller);
        }
        productService.saveProduct(product);
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "product-details";
    }
}
