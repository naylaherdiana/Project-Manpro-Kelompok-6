package com.selarasorganizer.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.selarasorganizer.model.EventDashboard;
import com.selarasorganizer.repository.AsistenRepository;
import com.selarasorganizer.repository.EventRepository;
import com.selarasorganizer.repository.KlienRepository;
import com.selarasorganizer.repository.VendorRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class PemilikController {
    private final KlienRepository klienRepository;
    private final AsistenRepository asistenRepository;
    private final EventRepository eventRepository;
    private final VendorRepository vendorRepository;

    public PemilikController(KlienRepository klienRepository, AsistenRepository asistenRepository, EventRepository eventRepository, VendorRepository vendorRepository) {
        this.klienRepository = klienRepository;
        this.asistenRepository = asistenRepository;
        this.eventRepository = eventRepository;
        this.vendorRepository = vendorRepository;
    }

    @GetMapping("/dashboard-pemilik")
    public String dashboard(HttpSession session, Model model){
        if (session.getAttribute("userRole") == null) {
            return "redirect:/login";
        }
        String role = (String) session.getAttribute("userRole");
        if (!"PEMILIK".equals(role)) {
            return "redirect:/login";
        }
        int totalKlien = klienRepository.countAll();
        int totalEvent = eventRepository.countByStatus("BERLANGSUNG");
        int totalVendor = vendorRepository.countAll();
        String vendorTeraktif = vendorRepository.findMostUsedVendorName();
        List<EventDashboard> upcomingEvents = eventRepository.findUpcomingEvents();

        model.addAttribute("totalKlien", totalKlien);
        model.addAttribute("totalEvent", totalEvent);
        model.addAttribute("totalVendor", totalVendor);
        model.addAttribute("vendorTeraktif", vendorTeraktif);
        model.addAttribute("upcomingEvents", upcomingEvents);
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