package com.panaderia.model;

public class Compra {
    private String producto;
    private int cantidad;

    // Constructor vacío
    public Compra() {}

    // Getters y Setters
    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
