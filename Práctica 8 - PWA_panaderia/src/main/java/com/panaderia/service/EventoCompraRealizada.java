package com.panaderia.service;

import org.springframework.context.ApplicationEvent;

public class EventoCompraRealizada extends ApplicationEvent {

    private String mensaje;

    public EventoCompraRealizada(String mensaje) {
        super(mensaje);
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }
}
