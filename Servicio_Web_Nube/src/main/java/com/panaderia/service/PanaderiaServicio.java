package com.panaderia.service;

import com.panaderia.model.Compra;
import com.panaderia.model.Pan;
import com.panaderia.repository.PanRepositorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PanaderiaServicio {

    private static final Logger logger = LoggerFactory.getLogger(PanaderiaServicio.class);

    @Autowired
    private PanRepositorio panRepositorio;

    // Obtener todos los productos
    public List<Pan> obtenerProductos() {
        logger.info("Obteniendo lista completa de productos.");
        return panRepositorio.findAll();
    }

    // Obtener el stock de un producto específico
    public int obtenerStock(String producto) {
        Pan pan = panRepositorio.findByProducto(producto);
        if (pan != null) {
            logger.info("Obteniendo stock de producto: '{}'. Stock disponible: {}", producto, pan.getStock());
            return pan.getStock();
        } else {
            logger.warn("Producto '{}' no encontrado en el inventario.", producto);
            return 0;
        }
    }

    // Realizar la compra
    public String comprarPan(String nombreCliente, List<Compra> compras) {
        StringBuilder resumen = new StringBuilder();
        int totalCompra = 0;
        boolean compraExitosa = false;

        // Si no hay productos en el carrito, se devuelve un mensaje de error
        if (compras == null || compras.isEmpty()) {
            logger.warn("El cliente {} intentó realizar una compra sin productos seleccionados.", nombreCliente);
            return "No se han seleccionado productos para la compra.";
        }

        for (Compra compra : compras) {
            Pan pan = panRepositorio.findByProducto(compra.getProducto());

            if (pan != null) {
                int stockDisponible = pan.getStock();
                int cantidadSolicitada = compra.getCantidad();

                if (stockDisponible >= cantidadSolicitada) {
                    // Si hay stock suficiente, se actualiza el stock
                    int stockRestante = stockDisponible - cantidadSolicitada;
                    pan.setStock(stockRestante);
                    panRepositorio.save(pan);

                    // Log de la compra realizada
                    logger.info("Usuario: {} compró {} unidades de '{}'. Stock restante: {}",
                            nombreCliente, cantidadSolicitada, compra.getProducto(), stockRestante);

                    // Añadir al resumen
                    totalCompra += cantidadSolicitada;
                    resumen.append(compra.getProducto())
                            .append(": ")
                            .append(cantidadSolicitada)
                            .append(" unidades compradas. Stock restante: ")
                            .append(stockRestante)
                            .append("\n");

                    compraExitosa = true;
                } else {
                    // Si no hay suficiente stock
                    logger.warn("Compra fallida para el cliente '{}'. Producto: '{}', cantidad solicitada: {}, stock disponible: {}",
                            nombreCliente, compra.getProducto(), cantidadSolicitada, stockDisponible);

                    resumen.append(compra.getProducto())
                            .append(": Stock insuficiente o producto no encontrado.\n");
                }
            } else {
                // Producto no encontrado
                logger.error("Producto '{}' no encontrado en la base de datos para el cliente '{}'.", compra.getProducto(), nombreCliente);
                resumen.append(compra.getProducto())
                        .append(": Producto no encontrado en el inventario.\n");
            }
        }

        // Resultado final de la compra
        if (compraExitosa) {
            resumen.append("Compra exitosa. Total de productos comprados: ")
                   .append(totalCompra);
        } else {
            resumen.append("No se pudo realizar la compra. Verifique el stock o los productos seleccionados.");
        }

        // Log del resultado final de la compra
        logger.info("Compra realizada por {}. Total de productos comprados: {}", nombreCliente, totalCompra);

        return resumen.toString();
    }

    // Método que simula el horneo de los panes cada 2 minutos
    @Scheduled(fixedRate = 300000)  // 300000 milisegundos = 5 minutos
    public void restablecerStock() {
        // Definir la cantidad de panes a hornear por tipo de pan
        Map<String, Integer> stockPorTipo = new HashMap<>();
        stockPorTipo.put("Pan de avena", 15);    
        stockPorTipo.put("Pan de trigo", 20);    
        stockPorTipo.put("Pan de centeno", 10);  
        stockPorTipo.put("Pan integral", 25);    
        // podemos añadir más tipos de panes según sea necesario
        stockPorTipo.put("Pan de maíz", 30);   
        stockPorTipo.put("Pan", 10); // Pan común

        // Obtener todos los panes en inventario
        Iterable<Pan> panes = panRepositorio.findAll();

        for (Pan pan : panes) {
            Integer stockRestablecer = stockPorTipo.get(pan.getProducto());
            if (stockRestablecer != null) {
                // Restablecer el stock de cada pan según lo definido arriba
                pan.setStock(pan.getStock() + stockRestablecer); // Sumar el stock
                panRepositorio.save(pan); // Guardar el producto actualizado
                logger.info("Stock de {} restablecido. Nuevo stock: {}", pan.getProducto(), pan.getStock());
            }
        }
    }
}
