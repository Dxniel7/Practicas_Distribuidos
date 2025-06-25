package com.panaderia.compra.controller;

import com.panaderia.compra.model.CompraRequest;
import com.panaderia.compra.service.CompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/compras")
public class CompraController {

    private final CompraService compraService;

    @Autowired
    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    @PostMapping
    public String realizarCompra(@RequestBody CompraRequest compraRequest) {
        return compraService.procesarCompra(compraRequest);
    }
}
