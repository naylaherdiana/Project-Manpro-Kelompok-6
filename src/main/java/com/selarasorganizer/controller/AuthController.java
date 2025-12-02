package com.selarasorganizer.controller;

import com.selarasorganizer.model.LoginRequest;
import com.selarasorganizer.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/")
    public String home() {
        return "layout/home-page";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login-page";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginRequest") LoginRequest request, BindingResult bindingResult, HttpSession session, Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/login-page";
        }

        String role = authService.authenticate(request.getUsername(), request.getPassword());
        if (role != null) {
            session.setAttribute("userRole", role);
            session.setAttribute("username", request.getUsername());
            
            if ("PEMILIK".equals(role)) {
                return "redirect:/dashboard-pemilik";
            } else {
                return "redirect:/dashboard-asisten";
            }

        } else {
            model.addAttribute("error", "Username atau password salah");
            return "auth/login-page";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
    
}