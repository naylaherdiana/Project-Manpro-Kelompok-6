package com.selarasorganizer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class PemilikController {
    
    @GetMapping("/dashboard-pemilik")
    public String dashboard(HttpSession session, Model model){
        if (session.getAttribute("userRole") == null) {
            return "redirect:/login";
        }
        String role = (String) session.getAttribute("userRole");
        if (!"PEMILIK".equals(role)) {
            return "redirect:/login";
        }
        return "pemilik/dashboard-pemilik";
    }

    @GetMapping("/asisten-pemilik")
    public String daftarAsisten(HttpSession session, Model model){
        if (session.getAttribute("userRole") == null) {
            return "redirect:/login";
        }
        String role = (String) session.getAttribute("userRole");
        if (!"PEMILIK".equals(role)) {
            return "redirect:/login";
        }
        return "pemilik/asisten-pemilik";
    }

    @GetMapping("/vendor-pemilik")
    public String daftarVendor(HttpSession session, Model model){
        if (session.getAttribute("userRole") == null) {
            return "redirect:/login";
        }
        String role = (String) session.getAttribute("userRole");
        if (!"PEMILIK".equals(role)) {
            return "redirect:/login";
        }
        return "pemilik/vendor-pemilik";
    }

    @GetMapping("/jenis-vendor-pemilik")
    public String jenisVendor(HttpSession session, Model model){
        if (session.getAttribute("userRole") == null) {
            return "redirect:/login";
        }
        String role = (String) session.getAttribute("userRole");
        if (!"PEMILIK".equals(role)) {
            return "redirect:/login";
        }
        return "pemilik/jenis-vendor-pemilik";
    }

    @GetMapping("/laporan-pemilik")
    public String laporan(HttpSession session, Model model){
        if (session.getAttribute("userRole") == null) {
            return "redirect:/login";
        }
        String role = (String) session.getAttribute("userRole");
        if (!"PEMILIK".equals(role)) {
            return "redirect:/login";
        }
        return "pemilik/laporan-pemilik";
    }
}