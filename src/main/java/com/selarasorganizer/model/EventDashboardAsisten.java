package com.selarasorganizer.model;

import java.time.LocalDate;

import lombok.Data;

@Data
public class EventDashboardAsisten {
    private String namaEvent;
    private String namaKlien;
    private LocalDate tanggal;
    private String namaAsisten;
}   
