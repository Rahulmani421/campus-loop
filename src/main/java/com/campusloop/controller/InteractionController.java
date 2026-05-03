package com.campusloop.controller;

import com.campusloop.model.Message;
import com.campusloop.model.Product;
import com.campusloop.model.User;
import com.campusloop.service.MessageService;
import com.campusloop.service.ProductService;
import com.campusloop.service.UserService;
import com.campusloop.model.Notification;
import com.campusloop.repository.NotificationRepository;
import com.campusloop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class InteractionController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @PostMapping("/messages/send")
    public String sendMessage(@RequestParam Long receiverId, @RequestParam Long productId, @RequestParam String content, Principal principal) {
        if (principal == null) return "redirect:/login";
        
        User sender = userService.findByUsername(principal.getName()).get();
        User receiver = userRepository.findById(receiverId).get();
        Product product = productService.getProductById(productId);

        Message msg = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .relatedProduct(product)
                .content(content)
                .build();
        
        messageService.sendMessage(msg);
        return "redirect:/messages/chat?with=" + receiverId + "&productId=" + productId;
    }

    @GetMapping("/messages/chat")
    public String chat(@RequestParam Long with, @RequestParam Long productId, Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        User me = userService.findByUsername(principal.getName()).get();
        User other = userRepository.findById(with).get();
        Product product = productService.getProductById(productId);

        model.addAttribute("messages", messageService.getConversation(me, other));
        model.addAttribute("otherUser", other);
        model.addAttribute("product", product);
        model.addAttribute("me", me);
        return "chat";
    }

    @PostMapping("/products/{id}/save")
    public String saveProduct(@PathVariable Long id, Principal principal) {
        if (principal == null) return "redirect:/login";
        
        User user = userService.findByUsername(principal.getName()).get();
        Product product = productService.getProductById(id);
        
        user.getSavedProducts().add(product);
        userService.saveUser(user);
        
        return "redirect:/profile?saved=true";
    }

    @GetMapping("/messages")
    public String inbox(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        User user = userService.findByUsername(principal.getName()).get();
        model.addAttribute("messages", messageService.getInbox(user));
        return "inbox";
    }

    @GetMapping("/notifications")
    public String notifications(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        User user = userService.findByUsername(principal.getName()).get();
        model.addAttribute("allNotifications", notificationRepository.findByUserOrderByCreatedAtDesc(user));
        
        // Mark all as read when viewed
        notificationRepository.findByUserOrderByCreatedAtDesc(user).forEach(n -> {
            if (!n.isRead()) {
                n.setRead(true);
                notificationRepository.save(n);
            }
        });
        
        return "notifications";
    }
}
