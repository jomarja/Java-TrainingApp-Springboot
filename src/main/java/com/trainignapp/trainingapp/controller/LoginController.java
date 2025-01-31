package com.trainignapp.trainingapp.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Load the Thymeleaf login page
    }

    @GetMapping("/home")
    public String showHomePage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName()); // Pass username to Thymeleaf
        return "home"; // Load the Thymeleaf home page
    }

    @GetMapping("/logout")
    public String showLogoutPage() {
        return "logout"; // Returns the logout page
    }
}
