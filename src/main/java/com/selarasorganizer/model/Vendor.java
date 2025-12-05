package com.selarasorganizer.model;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class Vendor {
    private Long idvendor;
    private String namapemilik;
    private String namavendor;
    private String alamatvendor;
    private String kontakvendor;
    private Long idjenisvendor;

    private String namajenisvendor;
    private BigDecimal kisaranhargamin;
    private BigDecimal kisaranhargamax;
}