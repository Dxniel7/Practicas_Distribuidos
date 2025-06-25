package com.panaderia.controller;

import com.panaderia.model.CompraRequest;  // Nueva clase para encapsular nombreCliente y compras

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.panaderia.service.PanaderiaServicio;
import com.panaderia.model.Pan;

import java.util.List;

@RestController
@RequestMapping("/panaderia")
@CrossOrigin(origins = "*")
public class PanaderiaControlador {

    @Autowired
    private PanaderiaServicio panaderiaServicio;

    // Obtener lista de todos los productos disponibles
    @GetMapping("/productos")
    public List<Pan> obtenerProductos() {
        return panaderiaServicio.obtenerProductos();
    }

    // Ver el stock actual de un producto específico
    @GetMapping("/stock/{producto}")
    public int obtenerStock(@PathVariable String producto) {
        return panaderiaServicio.obtenerStock(producto);
    }

    // Realizar una compra
    @PostMapping("/comprar")
    public String comprarPan(@RequestBody CompraRequest request) {
        return panaderiaServicio.comprarPan(request.getNombreCliente(), request.getCompras());
    }
}
