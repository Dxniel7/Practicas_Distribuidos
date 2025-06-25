import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServidorDescubrimiento {
    private static final int PUERTO_DESCUBRIMIENTO = 5050;
    private static final Map<String, Integer> servidoresPanaderia = new ConcurrentHashMap<>(); // Mapa concurrente para manejo de múltiples hilos

    public static void main(String[] args) {
        // Iniciar el servidor de descubrimiento
        try (ServerSocket serverSocket = new ServerSocket(PUERTO_DESCUBRIMIENTO)) {
            System.out.println("Servidor de descubrimiento iniciado en el puerto " + PUERTO_DESCUBRIMIENTO);

            while (true) {
                // Aceptar las conexiones de los servidores de panadería
                Socket socketCliente = serverSocket.accept();
                new Thread(() -> manejarCliente(socketCliente)).start(); // Manejo concurrente de clientes
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Maneja la conexión de un servidor de panadería
    private static void manejarCliente(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
    
            // Recibir el mensaje del cliente
            String mensaje = in.readLine();
    
            // Dentro de manejarCliente(), en el caso de solicitud de puerto:
                if ("SOLICITUD_PUERTO".equals(mensaje)) {
                    if (!servidoresPanaderia.isEmpty()) {
                // Obtener todos los puertos registrados como una lista separada por comas
                    String puertos = String.join(", ", servidoresPanaderia.values().stream().map(String::valueOf).toArray(String[]::new));
                         out.println(puertos);  // Enviar la lista de puertos al cliente
                            System.out.println("Se ha enviado la lista de puertos de panadería: " + puertos);
                    } else {
                    out.println("No hay servidores de panadería disponibles.");
                    }
                } else {
                // Si el mensaje no es una solicitud de puerto, manejarlo como un registro de servidor
                int puerto = Integer.parseInt(mensaje); // Convertir el mensaje a puerto
                System.out.println("Servidor de panadería registrado en el puerto: " + puerto);
    
                // Registrar el servidor de panadería en el mapa (servidores disponibles)
                servidoresPanaderia.put("panaderia" + puerto, puerto);
    
                // Responder al servidor de panadería que se registró correctamente
                out.println("Servidor registrado correctamente en el puerto: " + puerto);
    
                // (Opcional) Imprimir todos los servidores registrados para verificación
                System.out.println("Servidores de panadería registrados: " + servidoresPanaderia);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    // Método para registrar un servidor (llamado desde el servidor de panadería)
    public static void registrarServidor(int puerto) {
        try (Socket socket = new Socket("localhost", PUERTO_DESCUBRIMIENTO);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            // Enviar el puerto del servidor de panadería al servidor de descubrimiento
            out.println(puerto); // Enviar el puerto como mensaje
            System.out.println("Servidor de panadería con puerto " + puerto + " se ha registrado.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para desregistrar un servidor (llamado desde el servidor de panadería)
    public static void desregistrarServidor(int puerto) {
        try (Socket socket = new Socket("localhost", PUERTO_DESCUBRIMIENTO);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            // Desregistrar el servidor de panadería
            servidoresPanaderia.remove("panaderia" + puerto);
            System.out.println("Servidor de panadería desregistrado en el puerto: " + puerto);
            out.println("Desregistro exitoso para el servidor en el puerto: " + puerto);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para obtener los servidores registrados (opcional)
    public static Map<String, Integer> obtenerServidoresRegistrados() {
        return new HashMap<>(servidoresPanaderia); // Retorna una copia del mapa para evitar cambios directos
    }
}
