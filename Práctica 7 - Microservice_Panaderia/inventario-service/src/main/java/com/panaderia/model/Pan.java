package com.panaderia.model;

import jakarta.persistence.*;

@Entity
@Table(name = "inventario")
public class Pan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String producto;
    private int stock;

    // Getters y Setters
    public Long getId() { return id; }
    public String getProducto() { return producto; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}
