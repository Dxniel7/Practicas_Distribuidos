import java.io.*;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.sql.*;

class InventarioDB {
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static final String URL = "jdbc:mysql://localhost:3306/panaderia?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static int obtenerStock() {
        int stock = 0;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT stock FROM inventario WHERE producto='Pan'")) {
            if (rs.next()) {
                stock = rs.getInt("stock");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stock;
    }

    public static synchronized boolean comprarPan(int cantidad) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false);

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT stock FROM inventario WHERE producto='Pan' FOR UPDATE")) {
                if (rs.next()) {
                    int stockActual = rs.getInt("stock");
                    if (stockActual >= cantidad) {
                        try (PreparedStatement pstmt = conn.prepareStatement(
                                "UPDATE inventario SET stock = stock - ? WHERE producto='Pan'")) {
                            pstmt.setInt(1, cantidad);
                            pstmt.executeUpdate();
                        }
                        conn.commit();
                        return true;
                    }
                }
            }
            conn.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void añadirStock(int cantidad) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("UPDATE inventario SET stock = stock + ? WHERE producto='Pan'")) {
            pstmt.setInt(1, cantidad);
            pstmt.executeUpdate();
            System.out.println("Se hornearon " + cantidad + " panes. Nuevo stock: " + obtenerStock());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class HornoPanadero extends Thread {
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(10000); // Cada 10 segundos se hornean más panes
                InventarioDB.añadirStock(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class HiloCliente extends Thread {
    private final Socket socket;

    public HiloCliente(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // Leer el nombre del cliente y dar la bienvenida
            String nombreCliente = in.readLine();
            String ipCliente = socket.getInetAddress().getHostAddress();
            System.out.println(nombreCliente + " se ha conectado desde la IP: " + ipCliente + " en el puerto: " + socket.getPort());
            out.println("Bienvenido a la Panadería, " + nombreCliente + "!");

            while (true) {
                // Enviar menú
                out.println("\n--- Menú Panadería ---");
                out.println("1. Ver stock");
                out.println("2. Comprar pan");
                out.println("3. Salir");
                out.println("Seleccione una opción:");

                String opcionStr = in.readLine();
                if (opcionStr == null) break;

                int opcion;
                try {
                    opcion = Integer.parseInt(opcionStr);
                } catch (NumberFormatException e) {
                    out.println("Opción no válida. Intente de nuevo.");
                    continue;
                }

                if (opcion == 1) {
                    out.println("Stock disponible: " + InventarioDB.obtenerStock() + " panes.");
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
                    boolean compraExitosa = InventarioDB.comprarPan(cantidad);
                    if (compraExitosa) {
                        out.println("Compra exitosa. Panes restantes: " + InventarioDB.obtenerStock());
                        System.out.println("Cliente " + nombreCliente + " compró " + cantidad + " panes. Stock restante: " + InventarioDB.obtenerStock());
                    } else {
                        out.println("No hay suficiente pan. Por favor, espere mientras horneamos más.");
                    }
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

public class PanaderiaServidor {
    public static void iniciarServidor(int puerto) {
        ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);

        // Registrar el servidor en el servidor de descubrimiento antes de iniciar el servicio
        ServidorDescubrimiento.registrarServidor(puerto); 

        new HornoPanadero().start(); // Hilo para hornear pan

        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor de panadería iniciado en el puerto " + puerto);

            while (true) {
                Socket socketCliente = serverSocket.accept();
                pool.execute(new HiloCliente(socketCliente));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Desregistrar el servidor en el servidor de descubrimiento cuando se detiene
            ServidorDescubrimiento.desregistrarServidor(puerto);
        }
    }

    public static void main(String[] args) {
        // Verificar que se pasen puertos como argumentos
        if (args.length == 0) {
            System.err.println("Debe proporcionar al menos un puerto.");
            return;
        }

        // Iterar sobre los puertos pasados y crear un hilo para cada uno
        for (String puertoStr : args) {
            try {
                int puerto = Integer.parseInt(puertoStr); // Convertir cada argumento a entero
                // Iniciar un hilo para cada puerto
                new Thread(() -> iniciarServidor(puerto)).start();
            } catch (NumberFormatException e) {
                System.err.println("Error: El puerto '" + puertoStr + "' no es válido.");
            }
        }
    }
}
