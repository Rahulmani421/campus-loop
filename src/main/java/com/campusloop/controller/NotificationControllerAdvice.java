package com.campusloop.controller;

import com.campusloop.model.User;
import com.campusloop.repository.NotificationRepository;
import com.campusloop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.security.Principal;

@ControllerAdvice
public class NotificationControllerAdvice {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void addNotificationAttributes(Model model, Principal principal) {
        if (principal != null) {
            User user = userService.findByUsername(principal.getName()).orElse(null);
            if (user != null) {
                long unreadCount = notificationRepository.countByUserAndIsReadFalse(user);
                model.addAttribute("unreadNotifications", unreadCount);
                model.addAttribute("notifications", notificationRepository.findByUserOrderByCreatedAtDesc(user).stream().limit(5).toList());
            }
        }
    }
}
