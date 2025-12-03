package com.selarasorganizer.controller;

import com.selarasorganizer.model.LoginRequest;
import com.selarasorganizer.model.RegisterRequest;
import com.selarasorganizer.service.AuthService;
import com.selarasorganizer.service.RegisterService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final AuthService authService;
    private final RegisterService registerService;

    public AuthController(AuthService authService, RegisterService registerService) {
        this.authService = authService;
        this.registerService = registerService;
    }

    // Halaman landing (sebelum login)
    @GetMapping("/")
    public String home() {
        return "layout/home-page";
    }

    // Tampilkan halaman login
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login-page";
    }

    // Proses login
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

    // Tampilkan halaman register (untuk asisten)
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register-page";
    }

    // Proses registrasi (hanya untuk asisten)
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest request, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/register-page";
        }

        boolean success = registerService.registerAsisten(request);
        if (success) {
            return "redirect:/login?registered";
        } else {
            model.addAttribute("error", "Username atau email sudah digunakan");
            return "auth/register-page";
        }
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
    
}