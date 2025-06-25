package com.panaderia.controller;

import com.panaderia.model.CompraRequest;
import com.panaderia.model.Pan;
import com.panaderia.service.EventoCompraRealizada;
import com.panaderia.service.EventoStockRepuesto;
import com.panaderia.service.PanaderiaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.ArrayList;

@RestController
@RequestMapping("/panaderia")
@CrossOrigin(origins = "*")
public class PanaderiaControlador {

    @Autowired
    private PanaderiaServicio panaderiaServicio;

    // Lista para mantener los clientes conectados por SSE
    private final List<SseEmitter> emisores = new CopyOnWriteArrayList<>();

    // Obtener lista de todos los productos disponibles
    @GetMapping("/productos")
    public List<Pan> obtenerProductos() {
        return panaderiaServicio.obtenerProductos();
    }

    // Ver el stock actual de un producto específico
    @GetMapping("/stock/{producto}")
    public int obtenerStock(@PathVariable String producto) {
        return panaderiaServicio.obtenerStock(producto);
    }

    // Realizar una compra
    @PostMapping("/comprar")
    public String comprarPan(@RequestBody CompraRequest request) {
        return panaderiaServicio.comprarPan(request.getNombreCliente(), request.getCompras());
    }

    // Endpoint SSE para que el frontend escuche actualizaciones en tiempo real
@GetMapping(value = "/eventos", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public SseEmitter emitirEventos() {
    SseEmitter emisor = new SseEmitter(Long.MAX_VALUE); // Emisor sin límite de tiempo
    emisores.add(emisor);

    emisor.onCompletion(() -> emisores.remove(emisor)); // Eliminar al completar
    emisor.onTimeout(() -> emisores.remove(emisor));    // Eliminar al expirar

    return emisor;
}

// Método para escuchar el evento de compra realizada y notificar a los clientes
@EventListener
public void onCompraRealizada(EventoCompraRealizada evento) {
    String mensaje = evento.getMensaje();
    List<SseEmitter> emisoresAEliminar = new ArrayList<>();

    for (SseEmitter emisor : emisores) {
        try {
            emisor.send(SseEmitter.event().data(mensaje));
        } catch (IOException e) {
            // En caso de error, marcamos el emisor para ser eliminado
            emisoresAEliminar.add(emisor);
        }
    }

    // Eliminar los emisores que no pudieron enviar el mensaje
    emisores.removeAll(emisoresAEliminar);
}

// Método para escuchar eventos de reposición de stock y notificar a los clientes
@EventListener
public void onReposicionStock(EventoStockRepuesto evento) {
    String mensaje = evento.getMensaje();
    List<SseEmitter> emisoresAEliminar = new ArrayList<>();

    for (SseEmitter emisor : emisores) {
        try {
            emisor.send(SseEmitter.event().data(mensaje));
        } catch (IOException e) {
            emisoresAEliminar.add(emisor);
        }
    }

    emisores.removeAll(emisoresAEliminar);
}

}