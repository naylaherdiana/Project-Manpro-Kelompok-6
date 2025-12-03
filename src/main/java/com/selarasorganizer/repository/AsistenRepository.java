package com.selarasorganizer.repository;

import com.selarasorganizer.model.RegisterRequest;

public interface AsistenRepository {
    boolean saveAsisten(RegisterRequest request);
}