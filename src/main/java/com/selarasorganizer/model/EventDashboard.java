package com.selarasorganizer.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EventDashboard {
    private Long idevent;
    private String namaevent;
    private LocalDate tanggal;
    private String statusevent;
    private String namaKlien;
    private String namaAsisten;
}
