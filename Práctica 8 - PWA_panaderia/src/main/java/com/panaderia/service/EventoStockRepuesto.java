package com.panaderia.service;

public class EventoStockRepuesto {
    private final String mensaje;

    public EventoStockRepuesto(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }
}
