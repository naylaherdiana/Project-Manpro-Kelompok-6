package com.selarasorganizer.model;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class Menangani {
    private Long idasisten;
    private Long idevent;
    private Long idvendor;
    private BigDecimal hargadealing;
    private String statusdealing;
    
    // Untuk join
    private String namavendor;
    private String namaevent;
    private String namajenisvendor;
}