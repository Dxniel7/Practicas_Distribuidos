import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

// Clase que representa la panadería en el servidor
class Panaderia {
    private int stockPan;

    public Panaderia(int stockInicial) {
        this.stockPan = stockInicial;
    }

    public synchronized String comprarPan(int cantidad, String cliente) {
        if (stockPan < cantidad) {
            System.out.println(cliente + " no tiene suficiente pan. Esperando...");
            // Notificar de inmediato al cliente que debe esperar mientras se hornean más panes.
            return "No hay suficiente pan. Por favor, espere mientras horneamos más.";
        }
    
        // Si hay suficiente pan, lo compramos
        stockPan -= cantidad;
        System.out.println(cliente + " compró " + cantidad + " panes. Panes restantes: " + stockPan);
        return "Compra exitosa. Panes restantes: " + stockPan;
    }
    


    public synchronized int getStockPan() {
        return stockPan;
    }

    public synchronized void añadirStock(int cantidad) {
        stockPan += cantidad;
        System.out.println("Se hornearon " + cantidad + " panes. Nuevo stock: " + stockPan);
        notifyAll(); // Despierta a los clientes en espera
    }
}

// Hilo que maneja la producción de pan cada cierto tiempo
class HornoPanadero extends Thread {
    private final Panaderia panaderia;

    public HornoPanadero(Panaderia panaderia) {
        this.panaderia = panaderia;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(10000); // Cada 10 segundos se hornean más panes
                panaderia.añadirStock(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

// Hilo para manejar cada cliente
class HiloCliente extends Thread {
    private final Socket socket;
    private final Panaderia panaderia;

    public HiloCliente(Socket socket, Panaderia panaderia) {
        this.socket = socket;
        this.panaderia = panaderia;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // Leer el nombre del cliente y dar la bienvenida
            String nombreCliente = in.readLine();
            System.out.println(nombreCliente + " se ha conectado.");
            out.println("Bienvenido a la Panadería, " + nombreCliente + "!");

            while (true) {
                // Enviar menú sin que se repita innecesariamente
                out.println("\n--- Menú Panadería ---");
                out.println("1. Ver stock");
                out.println("2. Comprar pan");
                out.println("3. Salir");
                out.println("Seleccione una opción:");

                String opcionStr = in.readLine();
                if (opcionStr == null) break; // Cliente cerró la conexión

                int opcion;
                try {
                    opcion = Integer.parseInt(opcionStr);
                } catch (NumberFormatException e) {
                    out.println("Opción no válida. Intente de nuevo.");
                    continue;
                }

                if (opcion == 1) {
                    out.println("Stock disponible: " + panaderia.getStockPan() + " panes.");
                } else if (opcion == 2) {
                    out.println("Ingrese la cantidad de pan que desea comprar:");
                    String cantidadStr = in.readLine();
                    int cantidad;
                    try {
                        cantidad = Integer.parseInt(cantidadStr);
                    } catch (NumberFormatException e) {
                        out.println("Cantidad no válida. Intente de nuevo.");
                        continue;
                    }
                    String respuesta = panaderia.comprarPan(cantidad, nombreCliente);
                    out.println(respuesta);
                } else if (opcion == 3) {
                    out.println("Gracias por visitar la panadería. ¡Hasta luego!");
                    System.out.println(nombreCliente + " se ha desconectado.");
                    break;
                } else {
                    out.println("Opción no válida. Intente de nuevo.");
                }
            }
        } catch (IOException e) {
            System.err.println("Error en la conexión con el cliente: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

// Servidor principal
public class PanaderiaServidor {
    public static void main(String[] args) {
        int puerto = 5000;
        Panaderia panaderia = new Panaderia(15);
        ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);

        new HornoPanadero(panaderia).start(); // Hilo para hornear pan

        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor de panadería iniciado en el puerto " + puerto);

            while (true) {
                Socket socketCliente = serverSocket.accept();
                pool.execute(new HiloCliente(socketCliente, panaderia));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
