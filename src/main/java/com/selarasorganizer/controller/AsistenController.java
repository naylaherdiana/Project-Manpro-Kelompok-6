package com.selarasorganizer.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.selarasorganizer.model.EventDashboardAsisten;
import com.selarasorganizer.repository.AsistenRepository;
import com.selarasorganizer.repository.EventRepository;
import com.selarasorganizer.repository.KlienRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AsistenController {

    private final AsistenRepository asistenRepository;
    private final KlienRepository klienRepository;
    private final EventRepository eventRepository;
    
    public AsistenController(AsistenRepository asistenRepository, EventRepository eventRepository, KlienRepository klienRepository) {
        this.asistenRepository = asistenRepository;
        this.klienRepository = klienRepository;
        this.eventRepository = eventRepository;
    }

    @GetMapping("/dashboard-asisten")
    public String dashboardAsisten(HttpSession session, Model model) {
        if (session.getAttribute("userRole") == null) {
            return "redirect:/login";
        }
        
        String role = (String) session.getAttribute("userRole");
        if (!"ASISTEN".equals(role)) {
            return "redirect:/login";
        }
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
  
        Long asistenId = asistenRepository.getAsistenIdByUserId(userId);
        if (asistenId == null) {
            session.setAttribute("error", "Data asisten tidak ditemukan");
            return "redirect:/login";
        }
        
        int totalEvent = eventRepository.countEventDitangani(asistenId);
        int totalKlien = klienRepository.countKlienDitangani(asistenId);
        int eventTuntas = eventRepository.countEventTuntas(asistenId);
        List<EventDashboardAsisten> eventBerlangsung = eventRepository.getEventBerlangsung(asistenId);
        
        model.addAttribute("totalEvent", totalEvent);
        model.addAttribute("totalKlien", totalKlien);
        model.addAttribute("eventTuntas", eventTuntas);
        model.addAttribute("eventBerlangsung", eventBerlangsung);
        
        String namaAsisten = asistenRepository.getNamaAsistenByUserId(userId);
        if (namaAsisten != null) {
            model.addAttribute("namaAsisten", namaAsisten);
            session.setAttribute("nama", namaAsisten); // Simpan di session juga
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