package com.panaderia.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {

    @Autowired
    private ApplicationEventPublisher publisher;

    // Publica un evento
    public void publicarEvento(String mensaje) {
        // Crear un evento con el mensaje
        publisher.publishEvent(new EventoCompraRealizada(mensaje));
    }
}
