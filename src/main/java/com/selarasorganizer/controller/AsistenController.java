package com.selarasorganizer.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.selarasorganizer.model.EventDashboardAsisten;
import com.selarasorganizer.model.Klien;
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
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            String namaAsisten = asistenRepository.getNamaAsistenByUserId(userId);
            if (namaAsisten != null) {
                model.addAttribute("namaAsisten", namaAsisten);
            }
        }
        List<Klien> klienList = klienRepository.findAll();
        model.addAttribute("klienList", klienList);
        
        return "asisten/klien-asisten";
    }

    @PostMapping("/klien-asisten/tambah")
    public String tambahKlien(@RequestParam String namaklien, @RequestParam(required = false) String alamatklien, @RequestParam String kontakklien, HttpSession session) {
        if (session.getAttribute("userRole") == null || !"ASISTEN".equals(session.getAttribute("userRole"))) {
            return "redirect:/login";
        }
        
        if (namaklien == null || namaklien.trim().isEmpty()) {
            session.setAttribute("error", "Nama klien tidak boleh kosong");
            return "redirect:/klien-asisten";
        }
        
        if (kontakklien == null || kontakklien.trim().isEmpty()) {
            session.setAttribute("error", "Kontak tidak boleh kosong");
            return "redirect:/klien-asisten";
        }
        
        Klien klien = new Klien();
        klien.setNamaklien(namaklien.trim());
        klien.setAlamatklien(alamatklien != null ? alamatklien.trim() : null);
        klien.setKontakklien(kontakklien.trim());
        
        klienRepository.save(klien);
        
        session.setAttribute("success", "Klien berhasil ditambahkan");
        return "redirect:/klien-asisten";
    }

    @PostMapping("/klien-asisten/edit")
    public String editKlien(@RequestParam Long idklien, @RequestParam String namaklien, @RequestParam(required = false) String alamatklien, @RequestParam String kontakklien, HttpSession session) {
        if (session.getAttribute("userRole") == null || !"ASISTEN".equals(session.getAttribute("userRole"))) {
            return "redirect:/login";
        }

        if (namaklien == null || namaklien.trim().isEmpty()) {
            session.setAttribute("error", "Nama klien tidak boleh kosong");
            return "redirect:/klien-asisten";
        }
        
        if (kontakklien == null || kontakklien.trim().isEmpty()) {
            session.setAttribute("error", "Kontak tidak boleh kosong");
            return "redirect:/klien-asisten";
        }
        
        Klien existingKlien = klienRepository.findById(idklien);
        if (existingKlien == null) {
            session.setAttribute("error", "Klien tidak ditemukan");
            return "redirect:/klien-asisten";
        }

        existingKlien.setNamaklien(namaklien.trim());
        existingKlien.setAlamatklien(alamatklien != null ? alamatklien.trim() : null);
        existingKlien.setKontakklien(kontakklien.trim());
        
        klienRepository.save(existingKlien);
        
        session.setAttribute("success", "Klien berhasil diperbarui");
        return "redirect:/klien-asisten";
    }

    @PostMapping("/klien-asisten/hapus")
    public String hapusKlien(@RequestParam Long idklien, HttpSession session) {
        if (session.getAttribute("userRole") == null || !"ASISTEN".equals(session.getAttribute("userRole"))) {
            return "redirect:/login";
        }
        boolean deleted = klienRepository.deleteById(idklien);
        if (deleted) {
            session.setAttribute("success", "Klien berhasil dihapus");
        } else {
            session.setAttribute("error", "Gagal menghapus klien");
        }
        return "redirect:/klien-asisten";
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