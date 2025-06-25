import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.time.LocalDateTime;

public class PanaderiaServidorRMI extends UnicastRemoteObject implements PanaderiaRMI {
    private static final String URL = "jdbc:mysql://localhost:3306/panaderia?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    
    public PanaderiaServidorRMI() throws RemoteException {
        super(); 

        // Iniciar el hilo que generará panes en intervalos
        Thread hornoPanadero = new Thread(new HornoPanadero(this));
        hornoPanadero.start();
    }

    @Override
    public int obtenerStock() throws RemoteException {
        int stock = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT stock FROM inventario WHERE producto='Pan'")) {
                if (rs.next()) {
                    stock = rs.getInt("stock");
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return stock;
    }

    @Override
    public synchronized boolean comprarPan(String nombreCliente, int cantidad) throws RemoteException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

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
                            int nuevoStock = stockActual - cantidad;
                            System.out.println("[" + LocalDateTime.now() + "] Cliente '" + nombreCliente + "' compró " + cantidad + " panes. Stock restante: " + nuevoStock);
                            return true;
                        }
                    }
                }
                conn.rollback();
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void añadirStock(int cantidad) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement("UPDATE inventario SET stock = stock + ? WHERE producto='Pan'")) {
                pstmt.setInt(1, cantidad);
                pstmt.executeUpdate();

                // Capturar RemoteException aquí
                try {
                    int nuevoStock = obtenerStock(); // Obtener el stock actualizado después de la operación
                    System.out.println("Se hornearon " + cantidad + " panes. Nuevo stock: " + nuevoStock);
                } catch (RemoteException e) {
                    System.out.println("Error al obtener el stock: " + e.getMessage());
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // Implementación para registrar la conexión del cliente
    @Override
    public void registrarConexion(String nombreCliente, String ipCliente) throws RemoteException {
        System.out.println("[" + LocalDateTime.now() + "] Cliente conectado: " + nombreCliente + " desde la IP: " + ipCliente);
    }

    // Implementación para registrar la desconexión del cliente
    @Override
    public void registrarDesconexion(String nombreCliente) throws RemoteException {
        System.out.println("Cliente desconectado: " + nombreCliente);
    }

    // HornoPanadero que generará panes cada cierto tiempo
    private static class HornoPanadero implements Runnable {
        private final PanaderiaServidorRMI servidor;

        public HornoPanadero(PanaderiaServidorRMI servidor) {
            this.servidor = servidor;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(10000); // Espera 10 segundos
                    servidor.añadirStock(5);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}