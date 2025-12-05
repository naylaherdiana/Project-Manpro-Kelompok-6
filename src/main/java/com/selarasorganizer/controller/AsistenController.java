package com.selarasorganizer.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.selarasorganizer.model.EventDashboardAsisten;
import com.selarasorganizer.model.JenisVendor;
import com.selarasorganizer.model.Klien;
import com.selarasorganizer.model.Menangani;
import com.selarasorganizer.model.Vendor;
import com.selarasorganizer.model.Event;
import com.selarasorganizer.repository.AsistenRepository;
import com.selarasorganizer.repository.EventRepository;
import com.selarasorganizer.repository.KlienRepository;
import com.selarasorganizer.repository.BudgetingRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AsistenController {

    private final AsistenRepository asistenRepository;
    private final KlienRepository klienRepository;
    private final EventRepository eventRepository;
    private final BudgetingRepository budgetingRepository;
    
    public AsistenController(AsistenRepository asistenRepository, EventRepository eventRepository, KlienRepository klienRepository, BudgetingRepository budgetingRepository) {
        this.asistenRepository = asistenRepository;
        this.klienRepository = klienRepository;
        this.eventRepository = eventRepository;
        this.budgetingRepository = budgetingRepository;
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
       
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        Long asistenId = asistenRepository.getAsistenIdByUserId(userId);
        if (asistenId == null) {
            session.setAttribute("error", "Data asisten tidak ditemukan");
            return "redirect:/login";
        }
        
        String namaAsisten = asistenRepository.getNamaAsistenByUserId(userId);
        if (namaAsisten != null) {
            model.addAttribute("namaAsisten", namaAsisten);
        }
        
        List<Event> eventTuntas = eventRepository.findTuntasByAsisten(asistenId);
        List<Event> eventBerlangsung = eventRepository.findBerlangsungByAsisten(asistenId);
        
        model.addAttribute("eventTuntas", eventTuntas);
        model.addAttribute("eventBerlangsung", eventBerlangsung);
        
        List<Klien> klienList = klienRepository.findAll();
        model.addAttribute("klienList", klienList);
        
        return "asisten/event-asisten";
    }

    @PostMapping("/event-asisten/tambah")
    public String tambahEvent(@RequestParam String namaevent, @RequestParam String jenisevent, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggal, @RequestParam Integer jumlahundangan, @RequestParam String statusevent, @RequestParam Long idklien, HttpSession session) {
        if (session.getAttribute("userRole") == null || !"ASISTEN".equals(session.getAttribute("userRole"))) {
            return "redirect:/login";
        }
        
        if (namaevent == null || namaevent.trim().isEmpty()) {
            session.setAttribute("error", "Nama event tidak boleh kosong");
            return "redirect:/event-asisten";
        }
        
        if (tanggal == null) {
            session.setAttribute("error", "Tanggal tidak boleh kosong");
            return "redirect:/event-asisten";
        }
        
        Long userId = (Long) session.getAttribute("userId");
        Long asistenId = asistenRepository.getAsistenIdByUserId(userId);
        if (asistenId == null) {
            session.setAttribute("error", "Data asisten tidak ditemukan");
            return "redirect:/login";
        }

        Event event = new Event();
        event.setNamaevent(namaevent.trim());
        event.setJenisevent(jenisevent.trim());
        event.setTanggal(tanggal);
        event.setJumlahundangan(jumlahundangan);
        event.setStatusevent(statusevent.trim());
        event.setIdklien(idklien);
        event.setIdasisten(asistenId);
        
        // Simpan ke database
        eventRepository.save(event);
        
        session.setAttribute("success", "Event berhasil ditambahkan");
        return "redirect:/event-asisten";
    }

    @PostMapping("/event-asisten/edit")
    public String editEvent(@RequestParam Long idevent, @RequestParam String namaevent, @RequestParam String jenisevent, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggal, @RequestParam Integer jumlahundangan, @RequestParam String statusevent, @RequestParam Long idklien, HttpSession session) {
        if (session.getAttribute("userRole") == null || !"ASISTEN".equals(session.getAttribute("userRole"))) {
            return "redirect:/login";
        }
        
        Event existingEvent = eventRepository.findById(idevent);
        if (existingEvent == null) {
            session.setAttribute("error", "Event tidak ditemukan");
            return "redirect:/event-asisten";
        }
        
        existingEvent.setNamaevent(namaevent.trim());
        existingEvent.setJenisevent(jenisevent.trim());
        existingEvent.setTanggal(tanggal);
        existingEvent.setJumlahundangan(jumlahundangan);
        existingEvent.setStatusevent(statusevent.trim());
        existingEvent.setIdklien(idklien);
        
        eventRepository.save(existingEvent);
        
        session.setAttribute("success", "Event berhasil diperbarui");
        return "redirect:/event-asisten";
    }

    @PostMapping("/event-asisten/hapus")
    public String hapusEvent(@RequestParam Long idevent, HttpSession session) {
        if (session.getAttribute("userRole") == null || !"ASISTEN".equals(session.getAttribute("userRole"))) {
            return "redirect:/login";
        }
        
        boolean deleted = eventRepository.deleteById(idevent);
        
        if (deleted) {
            session.setAttribute("success", "Event berhasil dihapus");
        } else {
            session.setAttribute("error", "Gagal menghapus event");
        }
        
        return "redirect:/event-asisten";
    }

    @GetMapping("/budgeting-asisten")
    public String budgeting(HttpSession session, Model model, @RequestParam(required = false) BigDecimal hargaMin, @RequestParam(required = false) BigDecimal hargaMax) {
        if (session.getAttribute("userRole") == null) {
            return "redirect:/login";
        }
        if (!"ASISTEN".equals(session.getAttribute("userRole"))) {
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

        Double hargaMinDouble = hargaMin != null ? hargaMin.doubleValue() : null;
        Double hargaMaxDouble = hargaMax != null ? hargaMax.doubleValue() : null;

        List<JenisVendor> jenisVendorList = budgetingRepository.findAllJenisVendor(hargaMinDouble, hargaMaxDouble);
        model.addAttribute("jenisVendorList", jenisVendorList);
        
        List<Vendor> vendorList = budgetingRepository.searchVendorByHarga(hargaMinDouble, hargaMaxDouble);
        model.addAttribute("vendorList", vendorList);

        Map<Long, Integer> vendorCountMap = new HashMap<>();
        if (vendorList != null && !vendorList.isEmpty()) {
            for (Vendor vendor : vendorList) {
                Long jenisId = vendor.getIdjenisvendor();
                if (jenisId != null) {
                    vendorCountMap.put(jenisId, vendorCountMap.getOrDefault(jenisId, 0) + 1);
                }
            }
        }
        model.addAttribute("vendorCountMap", vendorCountMap);

        List<Event> eventList = budgetingRepository.findEventBerlangsungByAsisten(asistenId);
        model.addAttribute("eventList", eventList);
    
        List<Menangani> vendorEventList = new ArrayList<>();
        for (Event event : eventList) {
            List<Menangani> vendorsForEvent = budgetingRepository.findVendorByEvent(event.getIdevent());
            vendorEventList.addAll(vendorsForEvent);
        }
        model.addAttribute("vendorEventList", vendorEventList);
        
        model.addAttribute("hargaMin", hargaMin);
        model.addAttribute("hargaMax", hargaMax);
        
        return "asisten/budgeting-asisten";
    }

    @PostMapping("/budgeting-asisten/tambah-vendor")
    public String tambahVendorToEvent(@RequestParam Long idevent, @RequestParam Long idvendor,@RequestParam BigDecimal hargadealing, @RequestParam String statusdealing, HttpSession session) {
        if (session.getAttribute("userRole") == null || !"ASISTEN".equals(session.getAttribute("userRole"))) {
            return "redirect:/login";
        }

        if (hargadealing.compareTo(BigDecimal.ZERO) <= 0) { // Perbandingan BigDecimal
            session.setAttribute("error", "Harga dealing harus lebih dari 0");
            return "redirect:/budgeting-asisten";
        }
        
        // Ambil id asisten
        Long userId = (Long) session.getAttribute("userId");
        Long asistenId = asistenRepository.getAsistenIdByUserId(userId);
        if (asistenId == null) {
            session.setAttribute("error", "Data asisten tidak ditemukan");
            return "redirect:/login";
        }
        
        // Buat objek Menangani
        Menangani menangani = new Menangani();
        menangani.setIdasisten(asistenId);
        menangani.setIdevent(idevent);
        menangani.setIdvendor(idvendor);
        menangani.setHargadealing(hargadealing);
        menangani.setStatusdealing(statusdealing);
        
        // Simpan ke database
        budgetingRepository.addVendorToEvent(menangani);
        
        session.setAttribute("success", "Vendor berhasil ditambahkan ke event");
        return "redirect:/budgeting-asisten";
    }

    // Method untuk update harga dealing
    @PostMapping("/budgeting-asisten/update-harga")
    public String updateHargaDealing(@RequestParam Long idevent, @RequestParam Long idvendor, @RequestParam BigDecimal hargadealing, @RequestParam String statusdealing, HttpSession session) {
        if (session.getAttribute("userRole") == null || !"ASISTEN".equals(session.getAttribute("userRole"))) {
            return "redirect:/login";
        }
        
        // Validasi input
        if (hargadealing.compareTo(BigDecimal.ZERO) <= 0) {
            session.setAttribute("error", "Harga dealing harus lebih dari 0");
            return "redirect:/budgeting-asisten";
        }
        
        // Update harga dealing
        Menangani menangani = new Menangani();
        menangani.setIdevent(idevent);
        menangani.setIdvendor(idvendor);
        menangani.setHargadealing(hargadealing);
        menangani.setStatusdealing(statusdealing);
        
        budgetingRepository.updateHargaDealing(menangani);
        
        session.setAttribute("success", "Harga dealing berhasil diperbarui");
        return "redirect:/budgeting-asisten";
    }

    // Method untuk menghapus vendor dari event
    @PostMapping("/budgeting-asisten/hapus-vendor")
    public String hapusVendorFromEvent(
            @RequestParam Long idevent,
            @RequestParam Long idvendor,
            HttpSession session) {
        
        // Validasi session
        if (session.getAttribute("userRole") == null || !"ASISTEN".equals(session.getAttribute("userRole"))) {
            return "redirect:/login";
        }
        
        // Hapus vendor dari event
        boolean deleted = budgetingRepository.removeVendorFromEvent(idevent, idvendor);
        
        if (deleted) {
            session.setAttribute("success", "Vendor berhasil dihapus dari event");
        } else {
            session.setAttribute("error", "Gagal menghapus vendor dari event");
        }
        
        return "redirect:/budgeting-asisten";
    }

    @GetMapping("/budgeting-asisten/get-event-vendors")
    @ResponseBody
    public List<Menangani> getVendorsByEvent(@RequestParam Long eventId, HttpSession session) {
        if (session.getAttribute("userRole") == null || !"ASISTEN".equals(session.getAttribute("userRole"))) {
            return new ArrayList<>();
        }
        return budgetingRepository.findVendorByEvent(eventId);
    }

    @GetMapping("/budgeting-asisten/get-vendor-detail")
    @ResponseBody
    public Vendor getVendorDetail(@RequestParam Long vendorId, HttpSession session) {
        if (session.getAttribute("userRole") == null || !"ASISTEN".equals(session.getAttribute("userRole"))) {
            return null;
        }
        
        return budgetingRepository.findVendorById(vendorId);
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