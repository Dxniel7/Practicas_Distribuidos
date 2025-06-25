package com.panaderia.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CorrelationIdLoggerFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(CorrelationIdLoggerFilter.class);
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest httpRequest) {
            String correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER);
            if (correlationId != null) {
                logger.info("Recibido X-Correlation-ID: {}", correlationId);
            }
        }

        chain.doFilter(request, response);
    }
}
