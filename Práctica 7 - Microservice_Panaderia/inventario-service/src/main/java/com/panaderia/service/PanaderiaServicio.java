package com.panaderia.service;

import com.panaderia.model.Pan;
import com.panaderia.repository.PanRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PanaderiaServicio {

    @Autowired
    private PanRepositorio panRepositorio;

    // Obtener todos los productos
    public List<Pan> obtenerProductos() {
        return panRepositorio.findAll();
    }

    // Obtener el stock de un producto específico
    public int obtenerStock(String producto) {
        Pan pan = panRepositorio.findByProducto(producto);
        if (pan != null) {
            return pan.getStock();
        } else {
            return 0;
        }
    }

    // Realizar una compra y actualizar el stock
    public boolean realizarCompra(String producto, int cantidad) {
        Pan pan = panRepositorio.findByProducto(producto);
        if (pan != null && pan.getStock() >= cantidad) {
            pan.setStock(pan.getStock() - cantidad);
            panRepositorio.save(pan); // Actualizar en la base de datos
            return true;
        } else {
            return false; // No hay suficiente stock o el producto no existe
        }
    }

    // Abastecer el stock de un producto específico (sumando al stock actual)
    public void abastecerStock(String producto, int cantidad) {
    Pan pan = panRepositorio.findByProducto(producto);
    if (pan != null) {
        pan.setStock(pan.getStock() + cantidad); // Sumar la cantidad al stock actual
        panRepositorio.save(pan); // Guardar los cambios
    }
}


}
