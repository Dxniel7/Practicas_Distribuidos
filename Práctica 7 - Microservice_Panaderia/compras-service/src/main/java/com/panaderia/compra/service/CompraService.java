package com.panaderia.compra.service;

import com.panaderia.compra.model.Compra;
import com.panaderia.compra.model.CompraRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.cloud.client.ServiceInstance;

@Service
public class CompraService {

    private static final Logger logger = LoggerFactory.getLogger(CompraService.class);

    private final RestTemplate restTemplate;
    private final LoadBalancerClient loadBalancerClient; // Inyectar LoadBalancerClient

    private final String INVENTARIO_URL = "http://inventario-service/inventario";

    
    public CompraService(RestTemplate restTemplate, LoadBalancerClient loadBalancerClient) {
        this.restTemplate = restTemplate;
        this.loadBalancerClient = loadBalancerClient;
    }

    public String procesarCompra(CompraRequest compraRequest) {
        StringBuilder resultado = new StringBuilder("Resultados de compra:\n");
    
        for (Compra compra : compraRequest.getCompras()) {
            String producto = compra.getProducto();
            int cantidadDeseada = compra.getCantidad();
    
            // Log de selección del servidor por Ribbon
            ServiceInstance instance = loadBalancerClient.choose("inventario-service");
            if (instance != null) {
                logger.info("Load balancer for 'inventario-service' chose server: {}:{}", instance.getHost(), instance.getPort());
            } else {
                logger.error("No available instances for 'inventario-service' found!");
            }
    
            // Obtener stock actual desde el inventario-service
            Integer stockActual = restTemplate.getForObject(
                    INVENTARIO_URL + "/stock/" + producto,
                    Integer.class
            );
    
            if (stockActual != null && stockActual >= cantidadDeseada) {
                // Realizar la compra
                restTemplate.put(
                        INVENTARIO_URL + "/comprar/" + producto + "/" + cantidadDeseada,
                        null
                );
    
                // Consultar el nuevo stock después de la compra
                Integer stockRestante = restTemplate.getForObject(
                        INVENTARIO_URL + "/stock/" + producto,
                        Integer.class
                );
    
                logger.info("Cliente '{}' compró {} unidades de '{}'. Stock previo: {}, stock restante: {}",
                        compraRequest.getNombreCliente(), cantidadDeseada, producto, stockActual, stockRestante);
    
                resultado.append("Compra de ").append(cantidadDeseada)
                        .append(" ").append(producto).append(" realizada con éxito. ")
                        .append("Stock restante: ").append(stockRestante).append("\n");
    
            } else {
                logger.warn("Cliente '{}' intentó comprar {} unidades de '{}', pero el stock era de {}.",
                        compraRequest.getNombreCliente(), cantidadDeseada, producto,
                        stockActual != null ? stockActual : "desconocido");
    
                resultado.append("Stock insuficiente para ").append(producto).append(".\n");
            }
        }
    
        return resultado.toString();
    }
    
}
