package com.selarasorganizer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class AsistenController {

    @GetMapping("/dashboard-asisten")
    public String dashboard(HttpSession session, Model model){
        if (session.getAttribute("userRole") == null) {
            return "redirect:/login";
        }
        if (!"ASISTEN".equals(session.getAttribute("userRole"))) {
            return "redirect:/login";
        }
        return "asisten/dashboard-asisten";
    }

    @GetMapping("/klien-asisten")
    public String daftarKlien(HttpSession session, Model model){
        if (session.getAttribute("userRole") == null) {
            return "redirect:/login";
        }
        if (!"ASISTEN".equals(session.getAttribute("userRole"))) {
            return "redirect:/login";
        }
        return "asisten/klien-asisten";
    }

    @GetMapping("/event-asisten")
    public String daftarEvent(HttpSession session, Model model){
        if (session.getAttribute("userRole") == null) {
            return "redirect:/login";
        }
        if (!"ASISTEN".equals(session.getAttribute("userRole"))) {
            return "redirect:/login";
        }
        return "asisten/event-asisten";
    }

    @GetMapping("/budgeting-asisten")
    public String budgeting(HttpSession session, Model model){
        if (session.getAttribute("userRole") == null) {
            return "redirect:/login";
        }
        if (!"ASISTEN".equals(session.getAttribute("userRole"))) {
            return "redirect:/login";
        }
        return "asisten/budgeting-asisten";
    }

    @GetMapping("/laporan-asisten")
    public String laporan(HttpSession session, Model model){
        if (session.getAttribute("userRole") == null) {
            return "redirect:/login";
        }
        if (!"ASISTEN".equals(session.getAttribute("userRole"))) {
            return "redirect:/login";
        }
        return "asisten/laporan-asisten";
    }
}