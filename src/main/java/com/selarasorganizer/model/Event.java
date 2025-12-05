package com.selarasorganizer.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Event {
    private Long idevent;
    private String namaevent;
    private String jenisevent;
    private LocalDate tanggal;
    private Integer jumlahundangan;
    private String statusevent;
    private Long idklien;
    private Long idasisten;
    private String namaklien;
}