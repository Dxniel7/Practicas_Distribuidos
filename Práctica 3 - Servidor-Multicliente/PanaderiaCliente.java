import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class PanaderiaCliente {
    public static void main(String[] args) {
        String servidor = "192.168.1.79"; // Dirección del servidor (cambiar o adaptar según sea necesario)
        int puerto = 5000; // Puerto del servidor

        try (Socket socket = new Socket(servidor, puerto);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Conectado a la panadería.");

            // Ingresar nombre del cliente
            System.out.print("Ingrese su nombre: ");
            String nombre = scanner.nextLine();
            out.println(nombre); // Enviar el nombre al servidor
            System.out.println(in.readLine()); // Mensaje de bienvenida del servidor

            while (true) {
                // Leer y mostrar el menú hasta que se reciba "Seleccione una opción:"
                String linea;
                while (!(linea = in.readLine()).contains("Seleccione una opción")) {
                    System.out.println(linea);
                }
                System.out.println(linea); // Imprime "Seleccione una opción:"

                // Leer opción del usuario
                System.out.print("Opción: ");
                String opcionStr = scanner.nextLine();
                out.println(opcionStr); // Enviar opción al servidor

                if (opcionStr.equals("1") || opcionStr.equals("3")) { 
                    // Si es ver stock o salir, recibir respuesta del servidor
                    System.out.println("Servidor: " + in.readLine());
                    if (opcionStr.equals("3")) break; // Salir del loop si elige salir
                } else if (opcionStr.equals("2")) { 
                    // Comprar pan
                    System.out.println(in.readLine()); // Esperar mensaje del servidor "Ingrese la cantidad..."
                    String cantidad = scanner.nextLine();
                    out.println(cantidad); // Enviar cantidad al servidor
                    String respuesta = in.readLine(); // Recibir respuesta de compra
                    System.out.println("Servidor: " + respuesta);
                } else {
                    System.out.println("Opcion no válida.");
                }
            }

            System.out.println("Saliendo de la panaderia... ¡Hasta luego!");

        } catch (IOException e) {
            System.err.println("Error de conexion: " + e.getMessage());
        }
    }
}
