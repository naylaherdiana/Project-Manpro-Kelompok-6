package com.selarasorganizer.model;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class JenisVendor {
    private Long idjenisvendor;
    private BigDecimal kisaranhargamin;
    private BigDecimal kisaranhargamax;
    private String namajenisvendor;
}