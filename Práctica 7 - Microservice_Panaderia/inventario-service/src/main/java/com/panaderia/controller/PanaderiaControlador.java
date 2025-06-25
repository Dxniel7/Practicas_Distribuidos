package com.panaderia.controller;

import com.panaderia.model.Pan;
import com.panaderia.service.PanaderiaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/inventario")
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

    // Realizar una compra y actualizar el stock
    @PutMapping("/comprar/{producto}/{cantidad}")
    public String comprarProducto(@PathVariable String producto, @PathVariable int cantidad) {
        boolean exito = panaderiaServicio.realizarCompra(producto, cantidad);
        if (exito) {
            return "Compra realizada exitosamente";
        } else {
            return "No hay suficiente stock disponible";
        }
    }
}
