import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class PanaderiaCliente {
    public static void main(String[] args) {
        String servidor = "localhost"; // Dirección del servidor
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
                // Se modificó esta sección para ser más robusta
                String menuCompleto = "";
                String linea;
                // Leer líneas hasta que el menú esté completo o la conexión se cierre
                while ((linea = in.readLine()) != null && !linea.contains("Seleccione una opción")) {
                    menuCompleto += linea + "\n";
                }
                if (linea == null) { // Si la conexión se cerró inesperadamente
                    System.out.println("Servidor cerró la conexión.");
                    break;
                }
                menuCompleto += linea; // Añade la línea "Seleccione una opción:"
                System.out.println(menuCompleto); // Imprime el menú completo


                // Leer opción del usuario
                System.out.print("Opción: ");
                String opcionStr = scanner.nextLine();
                out.println(opcionStr); // Enviar opción al servidor

                if (opcionStr.equals("1")) {
                    // Si es ver stock, recibir respuesta del servidor
                    System.out.println("Servidor: " + in.readLine());
                } else if (opcionStr.equals("2")) {
                    // Comprar pan
                    // El servidor ya envía "Ingrese la cantidad..." como parte de su respuesta a la opción '2'
                    String promptCantidad = in.readLine(); // Leer "Ingrese la cantidad..."
                    if (promptCantidad == null) {
                        System.out.println("Error: Servidor no envió el prompt de cantidad.");
                        break;
                    }
                    System.out.println(promptCantidad);

                    String cantidad = scanner.nextLine();
                    out.println(cantidad); // Enviar cantidad al servidor
                    String respuesta = in.readLine(); // Recibir respuesta de compra
                    System.out.println("Servidor: " + respuesta);
                } else if (opcionStr.equals("3")) {
                    System.out.println("Servidor: " + in.readLine()); // Leer el mensaje de despedida del servidor
                    break; // Salir del loop si elige salir
                } else {
                    System.out.println("Servidor: " + in.readLine()); // Leer la respuesta de opción no válida
                }
            }

            System.out.println("Saliendo de la panaderia... ¡Hasta luego!");

        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }
}