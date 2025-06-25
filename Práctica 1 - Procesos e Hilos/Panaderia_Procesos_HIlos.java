import java.util.Random; 

// Clase que representa la panadería, con un stock limitado de panes
class Panaderia {
    private int stockPan; // Cantidad de panes disponibles

    // Constructor para inicializar la panadería con un stock inicial de panes
    public Panaderia(int stockPan) {
        this.stockPan = stockPan;
    }

    // Método sincronizado para realizar la compra de panes
    public void comprarPan(int cantidad, String cliente) {
        boolean successful = false; // Bandera para saber si la compra fue exitosa
        synchronized (this) { // Bloque sincronizado para evitar conflictos entre hilos, solo un cliente a la vez
            if (stockPan >= cantidad) { // Verifica si hay suficiente stock
                System.out.println(cliente + " está comprando " + cantidad + " panes.");
                stockPan -= cantidad; // Resta la cantidad comprada del stock
                System.out.println(cliente + " completó la compra. Panes restantes: " + stockPan);
                successful = true;
            } else {
                System.out.println(cliente + " intentó comprar " + cantidad + " panes, pero no hay suficiente stock.");
            }
        }
        // Si la compra fue exitosa, simulamos el tiempo de pago
        if (successful) {
            try {
                Thread.sleep(1000); // Simula el tiempo de transacción de 1 segundo
            } catch (InterruptedException e) {
                System.err.println("Hilo interrumpido: " + e.getMessage());
            }
        }
    }

    // Método para obtener el stock actual de panes
    public int getStockPan() {
        return stockPan;
    }
}

// Clase que repesenta a un cliente que compra pan, mediante un hilo
class Cliente extends Thread {
    private final Panaderia panaderia; 
    private final int cantidad; // Cantidad de pan que desea comprar
    private final String nombreCliente;
    private final Random random = new Random(); // Objeto para generar tiempos aleatorios

    // Constructor para inicializar el cliente con su nombre y la cantidad de pan que desea comprar
    public Cliente(Panaderia panaderia, int cantidad, String nombreCliente) {
        this.panaderia = panaderia;
        this.cantidad = cantidad;
        this.nombreCliente = nombreCliente;
    }

    // Método que se ejecuta al iniciar el hilo
    @Override
    public void run() {
        try {
            Thread.sleep(random.nextInt(5000)); // Simula que cada cliente llega en un momento diferente
        } catch (InterruptedException e) {
            System.err.println("Error en el cliente: " + e.getMessage());
        }
        panaderia.comprarPan(cantidad, nombreCliente); // Llamamos al método para comprar pan
     }
}

// Clase principal que simula una panadería con clientes comprando pan concurrentemente
public class PanaderiaConcurrente {
    public static void main(String[] args) {
        Panaderia panaderia = new Panaderia(15); // La panadería tiene 15 panes en stock al inicio

        // Seis clientes intentan comprar pan simultáneamente
        Thread cliente1 = new Cliente(panaderia, 4, "Cliente 1");
        Thread cliente2 = new Cliente(panaderia, 3, "Cliente 2");
        Thread cliente3 = new Cliente(panaderia, 5, "Cliente 3");
        Thread cliente4 = new Cliente(panaderia, 2, "Cliente 4");
        Thread cliente5 = new Cliente(panaderia, 3, "Cliente 5");
        Thread cliente6 = new Cliente(panaderia, 4, "Cliente 6"); // Puede que no alcance el stock

        // Iniciamos los hilos de los clientes
        cliente1.start();
        cliente2.start();
        cliente3.start();
        cliente4.start();
        cliente5.start();
        cliente6.start();

        // Esperamos a que todos los hilos terminen antes de mostrar el resultado final
        try {
            cliente1.join();
            cliente2.join();
            cliente3.join();
            cliente4.join();
            cliente5.join();
            cliente6.join();
        } catch (InterruptedException e) {
            System.err.println("Error en la simulación: " + e.getMessage());
        }

        // Se muestra el stock final después de todas las compras
        System.out.println("Ventas finalizadas. Panes restantes: " + panaderia.getStockPan());
    }
}
