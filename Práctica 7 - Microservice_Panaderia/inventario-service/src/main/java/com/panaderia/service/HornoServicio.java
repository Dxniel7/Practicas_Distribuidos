package com.panaderia.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

@Service
public class HornoServicio {

    private static final Logger logger = LoggerFactory.getLogger(HornoServicio.class); // Logger aquí

    private final PanaderiaServicio panaderiaServicio;

    public HornoServicio(PanaderiaServicio panaderiaServicio) {
        this.panaderiaServicio = panaderiaServicio;
    }

    // Se ejecuta cada hora (100000 ms = 100 segundos)
    @Scheduled(fixedRate = 100000)
    public void abastecerStockPanes() {
        List<String> productos = Arrays.asList(
                "Pan", "Pan de trigo", "Pan de centeno", "Pan integral",
                "Pan de avena", "Pan de maíz", "Donas"
        );

        for (String producto : productos) {
            panaderiaServicio.abastecerStock(producto, 20);
            int stockActual = panaderiaServicio.obtenerStock(producto);
            logger.info("Horno abastecido - Se añadieron {} unidades de '{}'. Stock actual: {}",
                    50, producto, stockActual);
        }
    }
}
