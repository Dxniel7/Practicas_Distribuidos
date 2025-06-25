package com.panaderia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.panaderia.model.Pan;

public interface PanRepositorio extends JpaRepository<Pan, Long> {
    Pan findByProducto(String producto);
}
