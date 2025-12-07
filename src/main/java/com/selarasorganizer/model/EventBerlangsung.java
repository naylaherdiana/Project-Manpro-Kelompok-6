package com.selarasorganizer.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EventBerlangsung {
    private Long idevent;
    private String namaevent;
    private LocalDate tanggal;
    private String namaAsisten;
    private String namaKlien;
}
