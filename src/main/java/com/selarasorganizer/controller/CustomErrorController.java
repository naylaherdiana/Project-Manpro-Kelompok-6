package com.selarasorganizer.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;

@Controller
@ControllerAdvice
@RequestMapping("/error")
public class CustomErrorController {

    // Handler untuk error yang di-throw oleh Spring
    @GetMapping
    public String handleHttpError(HttpServletRequest request, Model model, HttpSession session) {
        return processError(request, model, session);
    }
    
    // Global Exception Handler untuk DataIntegrityViolationException
    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrityViolation(DataIntegrityViolationException ex, 
                                               HttpServletRequest request,
                                               Model model,
                                               HttpSession session) {
        
        // Extract error message
        String errorMessage = extractConstraintMessage(ex);
        String path = request.getRequestURI();
        
        // Cek jika error dari hapus vendor/jenis vendor
        String referer = request.getHeader("Referer");
        if (referer != null) {
            if (referer.contains("/jenisvendor/") || referer.contains("/vendor/")) {
                errorMessage = "Tidak dapat menghapus data karena masih digunakan oleh data lain. " +
                              "Hapus atau ubah data yang terkait terlebih dahulu.";
            }
        }
        
        // Setup model untuk error 409 (Conflict)
        setupErrorModel(model, 409, "Konflik Data", errorMessage, path, session, ex);
        
        // Log error untuk debugging
        logErrorDetails(request, ex, model);
        
        return "error/error";
    }
    
    // Global Exception Handler untuk semua exception lainnya
    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception ex, 
                                      HttpServletRequest request,
                                      Model model,
                                      HttpSession session) {
        
        String errorMessage = ex.getMessage();
        String path = request.getRequestURI();
        
        // Setup model untuk error 500
        setupErrorModel(model, 500, "Kesalahan Server", errorMessage, path, session, ex);
        
        // Log error untuk debugging
        logErrorDetails(request, ex, model);
        
        return "error/error";
    }
    
    // Helper method untuk memproses error
    private String processError(HttpServletRequest request, Model model, HttpSession session) {
        // Get error attributes from request
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Exception exception = (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        String errorMessage = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        String path = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        
        // Default values
        if (statusCode == null) {
            statusCode = 500;
        }
        
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorMessage = getDefaultErrorMessage(statusCode);
        }
        
        if (path == null) {
            path = request.getRequestURI();
        }
        
        // Setup model
        setupErrorModel(model, statusCode, getErrorType(statusCode), errorMessage, path, session, exception);
        
        return "error/error";
    }
    
    // Helper method untuk setup model
    private void setupErrorModel(Model model, int statusCode, String errorType, 
                                 String errorMessage, String path, 
                                 HttpSession session, Exception exception) {
        
        model.addAttribute("statusCode", statusCode);
        model.addAttribute("errorType", errorType);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("path", path);
        model.addAttribute("timestamp", 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        
        // Session info
        if (session != null) {
            Object userRole = session.getAttribute("userRole");
            if (userRole != null) {
                model.addAttribute("session", session);
                model.addAttribute("userRole", userRole.toString());
            }
        }
        
        // Exception details for debugging
        if (exception != null) {
            model.addAttribute("exception", exception.getClass().getSimpleName());
            model.addAttribute("exceptionMessage", exception.getMessage());
            
            // Stack trace (hint)
            if (exception.getMessage() != null && exception.getMessage().contains("violates foreign key constraint")) {
                model.addAttribute("stackTraceHint", "Data masih direferensi oleh tabel lain. Periksa hubungan foreign key.");
            }
        }
    }
    
    // Helper method untuk extract constraint message
    private String extractConstraintMessage(DataIntegrityViolationException ex) {
        String message = ex.getMostSpecificCause().getMessage();
        
        if (message == null) {
            return "Data tidak dapat diproses karena melanggar aturan database.";
        }
        
        // Parse PostgreSQL error message
        if (message.contains("violates foreign key constraint")) {
            return "Data tidak dapat dihapus karena masih digunakan oleh data lain.";
        } else if (message.contains("unique constraint") || message.contains("duplicate key")) {
            return "Data dengan nilai yang sama sudah ada.";
        }
        
        return "Terjadi kesalahan integritas data.";
    }
    
    // Helper method untuk logging
    private void logErrorDetails(HttpServletRequest request, Exception ex, Model model) {
        StringBuilder details = new StringBuilder();
        details.append("Request Method: ").append(request.getMethod()).append("\n");
        details.append("Request URL: ").append(request.getRequestURL()).append("\n");
        details.append("Query String: ").append(request.getQueryString()).append("\n");
        details.append("Remote Addr: ").append(request.getRemoteAddr()).append("\n");
        
        // Add headers if needed
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (headerName.equals("Referer")) {
                details.append("Referer: ").append(request.getHeader(headerName)).append("\n");
            }
        }
        
        model.addAttribute("requestDetails", details.toString());
        
        // Log ke console untuk debugging
        System.err.println("=== ERROR DETAILS ===");
        System.err.println(details.toString());
        System.err.println("Exception: " + ex.getClass().getName());
        System.err.println("Message: " + ex.getMessage());
        System.err.println("=== END ERROR ===");
    }
    
    // Helper methods
    private String getErrorType(int statusCode) {
        return switch (statusCode) {
            case 400 -> "Permintaan Buruk";
            case 401 -> "Tidak Terotorisasi";
            case 403 -> "Akses Ditolak";
            case 404 -> "Halaman Tidak Ditemukan";
            case 405 -> "Metode Tidak Diizinkan";
            case 409 -> "Konflik Data";
            case 500 -> "Kesalahan Server Internal";
            default -> "Terjadi Kesalahan";
        };
    }
    
    private String getDefaultErrorMessage(int statusCode) {
        return switch (statusCode) {
            case 400 -> "Permintaan tidak dapat diproses oleh server.";
            case 401 -> "Anda perlu login untuk mengakses halaman ini.";
            case 403 -> "Anda tidak memiliki izin untuk mengakses halaman ini.";
            case 404 -> "Halaman yang Anda cari tidak ditemukan.";
            case 405 -> "Metode HTTP yang digunakan tidak diizinkan.";
            case 409 -> "Terjadi konflik dengan data yang sudah ada.";
            case 500 -> "Terjadi kesalahan internal pada server.";
            default -> "Terjadi kesalahan yang tidak terduga.";
        };
    }
}