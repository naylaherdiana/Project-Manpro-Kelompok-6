// src/main/java/com/selarasorganizer/service/RegisterService.java
package com.selarasorganizer.service;

import com.selarasorganizer.model.RegisterRequest;
import com.selarasorganizer.repository.AsistenRepository;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {

    private final AsistenRepository asistenRepository;

    public RegisterService(AsistenRepository asistenRepository) {
        this.asistenRepository = asistenRepository;
    }

    public boolean registerAsisten(RegisterRequest request) {
        return asistenRepository.saveAsisten(request);
    }
}