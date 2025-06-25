package com.panaderia.service;

import com.panaderia.model.Compra;
import com.panaderia.model.Pan;
import com.panaderia.repository.PanRepositorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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

    // Inyectamos el ApplicationEventPublisher para publicar eventos
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public List<Pan> obtenerProductos() {
        logger.info("Obteniendo lista completa de productos.");
        return panRepositorio.findAll();
    }

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

    public String comprarPan(String nombreCliente, List<Compra> compras) {
        StringBuilder resumen = new StringBuilder();
        int totalCompra = 0;
        boolean compraExitosa = false;

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
                    int stockRestante = stockDisponible - cantidadSolicitada;
                    pan.setStock(stockRestante);
                    panRepositorio.save(pan);

                    logger.info("Usuario: {} compró {} unidades de '{}'. Stock restante: {}",
                            nombreCliente, cantidadSolicitada, compra.getProducto(), stockRestante);

                    totalCompra += cantidadSolicitada;
                    resumen.append(compra.getProducto())
                            .append(": ")
                            .append(cantidadSolicitada)
                            .append(" unidades compradas. Stock restante: ")
                            .append(stockRestante)
                            .append("\n");

                    compraExitosa = true;

                    // Publicar el evento de compra
                    eventPublisher.publishEvent(new EventoCompraRealizada("🛒 " + nombreCliente + " compró " + cantidadSolicitada + " de " + compra.getProducto()));

                    // Notificar al cliente que el stock ha sido actualizado en tiempo real
                    actualizarStockYNotificar(compra.getProducto(), stockRestante);
                } else {
                    logger.warn("Compra fallida para el cliente '{}'. Producto: '{}', cantidad solicitada: {}, stock disponible: {}",
                            nombreCliente, compra.getProducto(), cantidadSolicitada, stockDisponible);

                    resumen.append(compra.getProducto())
                            .append(": Stock insuficiente o producto no encontrado.\n");
                }
            } else {
                logger.error("Producto '{}' no encontrado en la base de datos para el cliente '{}'.", compra.getProducto(), nombreCliente);
                resumen.append(compra.getProducto())
                        .append(": Producto no encontrado en el inventario.\n");
            }
        }

        if (compraExitosa) {
            resumen.append("Compra exitosa. Total de productos comprados: ")
                   .append(totalCompra);
        } else {
            resumen.append("No se pudo realizar la compra. Verifique el stock o los productos seleccionados.");
        }

        logger.info("Compra realizada por {}. Total de productos comprados: {}", nombreCliente, totalCompra);

        return resumen.toString();
    }

    // Método para notificar la actualización del stock
    private void actualizarStockYNotificar(String producto, int stockRestante) {
        // Notificar a los clientes conectados que el stock ha cambiado
        String mensaje = "El stock de '" + producto + "' ha sido actualizado. Stock restante: " + stockRestante;
        eventPublisher.publishEvent(new EventoCompraRealizada(mensaje));
    }

    // Método programado para restablecer el stock de panes cada 5 minutos
    @Scheduled(fixedRate = 300000) // Cada 5 minutos (300000 ms)
    public void restablecerStock() {
        Map<String, Integer> stockPorTipo = new HashMap<>();
        stockPorTipo.put("Pan de avena", 15);
        stockPorTipo.put("Pan de trigo", 20);
        stockPorTipo.put("Pan de centeno", 10);
        stockPorTipo.put("Pan integral", 25);
        stockPorTipo.put("Pan de maíz", 30);
        stockPorTipo.put("Pan", 10);

        Iterable<Pan> panes = panRepositorio.findAll();

        for (Pan pan : panes) {
            Integer stockRestablecer = stockPorTipo.get(pan.getProducto());
            if (stockRestablecer != null) {
                pan.setStock(pan.getStock() + stockRestablecer);
                panRepositorio.save(pan);
                logger.info("Stock de {} restablecido. Nuevo stock: {}", pan.getProducto(), pan.getStock());

                // Publicar evento de reposición de stock
                actualizarStockYNotificar(pan.getProducto(), pan.getStock());
            }
        }

        // Notificar que el inventario ha sido repuesto
        eventPublisher.publishEvent(new EventoStockRepuesto("El horno ha repuesto el inventario de panes"));
    }
}
