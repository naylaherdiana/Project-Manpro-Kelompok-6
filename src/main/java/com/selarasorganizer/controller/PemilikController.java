package com.selarasorganizer.controller;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.selarasorganizer.model.Asisten;
import com.selarasorganizer.model.EventDashboard;
import com.selarasorganizer.model.RegisterRequest;
import com.selarasorganizer.repository.AsistenRepository;
import com.selarasorganizer.repository.EventRepository;
import com.selarasorganizer.repository.KlienRepository;
import com.selarasorganizer.repository.UserRepository;
import com.selarasorganizer.repository.VendorRepository;
import java.security.SecureRandom;

import jakarta.servlet.http.HttpSession;

@Controller
public class PemilikController {
    private final UserRepository userRepository;
    private final KlienRepository klienRepository;
    private final AsistenRepository asistenRepository;
    private final EventRepository eventRepository;
    private final VendorRepository vendorRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public PemilikController(UserRepository userRepository, KlienRepository klienRepository, AsistenRepository asistenRepository, EventRepository eventRepository, VendorRepository vendorRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.klienRepository = klienRepository;
        this.asistenRepository = asistenRepository;
        this.eventRepository = eventRepository;
        this.vendorRepository = vendorRepository;
        this.passwordEncoder = passwordEncoder;
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
        List<Asisten> asistenList = asistenRepository.findAll();
        model.addAttribute("asistenList", asistenList);
        return "pemilik/asisten-pemilik";
    }

    @PostMapping("/asisten-pemilik")
    public String tambahAsisten(@RequestParam String nama, @RequestParam String alamat, @RequestParam String kontak, @RequestParam String email, HttpSession session, Model model) {
        if (!"PEMILIK".equals(session.getAttribute("userRole"))) {
            return "redirect:/login";
        }

        String username = nama.toLowerCase().replaceAll("\\s+", "");
        String password = generateRandomPassword(12);
    
        String hashedPassword = passwordEncoder.encode(password);
        RegisterRequest request = new RegisterRequest();
        request.setNama(nama);
        request.setAlamat(alamat);
        request.setKontak(kontak);
        request.setEmail(email);
        request.setUsername(username);
        request.setPassword(hashedPassword);

        boolean success = asistenRepository.saveAsisten(request);
        if (success) {
            return "redirect:/asisten-pemilik";
        } else {
            model.addAttribute("error", "Gagal menambah asisten (username/email sudah ada)");
            List<Asisten> asistenList = asistenRepository.findAll();
            model.addAttribute("asistenList", asistenList);
            return "pemilik/asisten-pemilik";
        }
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @PostMapping("/update-asisten")
    public String updateAsisten(@RequestParam Long id, @RequestParam String nama, @RequestParam String alamat, @RequestParam String kontak, @RequestParam String email, HttpSession session) {
        if (!"PEMILIK".equals(session.getAttribute("userRole"))) {
            return "redirect:/login";
        }

        Asisten asisten = asistenRepository.findByIdWithEmail(id);
        asisten.setNama(nama);
        asisten.setAlamat(alamat);
        asisten.setKontak(kontak);
        asisten.setEmail(email);
        asistenRepository.update(asisten);

        return "redirect:/asisten-pemilik";
    }

    @PostMapping("/delete-asisten")
    public String deleteAsisten(@RequestParam Long id, HttpSession session) {
        if (!"PEMILIK".equals(session.getAttribute("userRole"))) {
            return "redirect:/login";
        }
        userRepository.deleteById(id);
        return "redirect:/asisten-pemilik";
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