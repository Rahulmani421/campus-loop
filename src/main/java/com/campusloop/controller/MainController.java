package com.campusloop.controller;

import com.campusloop.model.Category;
import com.campusloop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String home(Model model, 
                       @RequestParam(required = false) String query,
                       @RequestParam(required = false) Category category) {
        if ((query != null && !query.isEmpty()) || category != null) {
            model.addAttribute("products", productService.searchProducts(query, category));
        } else {
            model.addAttribute("products", productService.getAllActiveProducts());
        }
        model.addAttribute("categories", Category.values());
        return "home";
    }
}
