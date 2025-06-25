package gateway_service.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;

@Component
public class CorrelationFilter implements GlobalFilter {

    private static final Logger logger = LoggerFactory.getLogger(CorrelationFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String correlationId = UUID.randomUUID().toString(); // Generar un ID único

        // Agregar encabezado a la solicitud
        exchange.getRequest().mutate()
                .header("X-Correlation-ID", correlationId)
                .build();

        // Log del ID de correlación
        logger.info("Encabezado X-Correlation-ID agregado: {}", correlationId);

        return chain.filter(exchange);
    }
}
