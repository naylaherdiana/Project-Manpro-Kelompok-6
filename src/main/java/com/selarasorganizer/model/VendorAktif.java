package com.selarasorganizer.model;

import lombok.Data;

@Data
public class VendorAktif {
    private Long idvendor;
    private String namavendor;
    private String namapemilik;
    private Integer jumlahEvent;
}