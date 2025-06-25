package com.panaderia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.panaderia.model.Pan;

// Metodo para consultar el stock de un producto por su nombre
public interface PanRepositorio extends JpaRepository<Pan, Long> {
    Pan findByProducto(String producto);
    
}
