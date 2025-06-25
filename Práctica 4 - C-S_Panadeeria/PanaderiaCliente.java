import java.io.*;
import java.net.*;
import java.util.*;

public class PanaderiaCliente {
    public static void main(String[] args) {
        String servidorDescubrimiento = "192.168.1.79"; // Dirección del servidor de descubrimiento
        int puertoDescubrimiento = 5050; // Puerto del servidor de descubrimiento

        final Socket[] socket = new Socket[1];  // Usamos un array para poder modificar su valor dentro del hook
        final BufferedReader[] in = new BufferedReader[1];  // Se declara como final
        final PrintWriter[] out = new PrintWriter[1];  // Se declara como final
        Scanner scanner = new Scanner(System.in);

        // Registrar un shutdown hook para manejar la interrupción del programa
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("Cerrando recursos correctamente...");
                if (socket[0] != null && !socket[0].isClosed()) {
                    socket[0].close();
                }
                if (in[0] != null) {
                    in[0].close();
                }
                if (out[0] != null) {
                    out[0].close();
                }
            } catch (IOException e) {
                System.err.println("Error cerrando la conexión: " + e.getMessage());
            }
        }));

        // Conectar al servidor de descubrimiento
        try {
            socket[0] = new Socket(servidorDescubrimiento, puertoDescubrimiento);  // Asignamos el socket en el array
            in[0] = new BufferedReader(new InputStreamReader(socket[0].getInputStream()));
            out[0] = new PrintWriter(socket[0].getOutputStream(), true);

            // Solicitar un puerto de servidor de panadería
            System.out.println("Solicitando un puerto de panadería...");
            out[0].println("SOLICITUD_PUERTO"); // Enviar solicitud para obtener un puerto de servidor

            // Leer la lista de puertos disponibles
            String respuesta = in[0].readLine();
            if (respuesta != null && !respuesta.equals("No hay servidores de panadería disponibles.")) {
                // Extraer los puertos de servidores disponibles (puedes modificar esto según el formato de la respuesta)
                List<Integer> puertosDisponibles = new ArrayList<>();
                String[] puertos = respuesta.split(",");
                for (String puertoStr : puertos) {
                    puertosDisponibles.add(Integer.parseInt(puertoStr.trim()));
                }

                // Elegir un puerto aleatoriamente
                Random random = new Random();
                int puertoPanaderia = puertosDisponibles.get(random.nextInt(puertosDisponibles.size()));
                System.out.println("Conectando al servidor de panadería en el puerto " + puertoPanaderia);

                // Ahora conectarse al servidor de panadería en el puerto asignado
                try (Socket panaderiaSocket = new Socket(servidorDescubrimiento, puertoPanaderia);
                     BufferedReader panaderiaIn = new BufferedReader(new InputStreamReader(panaderiaSocket.getInputStream()));
                     PrintWriter panaderiaOut = new PrintWriter(panaderiaSocket.getOutputStream(), true)) {

                    // Solicitar y validar el nombre del cliente
                    String nombre = null;
                    boolean nombreValido = false;

                    while (!nombreValido) {
                        System.out.print("Ingrese su nombre: ");
                        if (scanner.hasNextLine()) {
                            nombre = scanner.nextLine();
                        } else {
                            break; // Salir si no se puede leer del scanner
                        }
                        if (nombre != null && !nombre.trim().isEmpty()) {
                            nombreValido = true; // El nombre es válido
                        } else {
                            System.out.println("El nombre no puede estar vacío.");
                        }
                    }

                    // Si el nombre no es válido (null o vacío), no se envía nada
                    if (nombre != null && !nombre.trim().isEmpty()) {
                        panaderiaOut.println(nombre); // Enviar nombre al servidor
                    }

                    // Mensaje de bienvenida del servidor
                    String mensajeBienvenida = panaderiaIn.readLine();
                    if (mensajeBienvenida != null) {
                        System.out.println(mensajeBienvenida);
                    }

                    while (true) {
                        String linea;
                        while (!(linea = panaderiaIn.readLine()).contains("Seleccione una opción")) {
                            System.out.println(linea);
                        }
                        System.out.println(linea); // Imprime "Seleccione una opción:"

                        System.out.print("Opción: ");
                        String opcionStr = scanner.nextLine();
                        panaderiaOut.println(opcionStr); // Enviar opción al servidor

                        if (opcionStr.equals("1") || opcionStr.equals("3")) {
                            System.out.println("Servidor: " + panaderiaIn.readLine());
                            if (opcionStr.equals("3")) break; // Salir si elige salir
                        } else if (opcionStr.equals("2")) {
                            System.out.println(panaderiaIn.readLine());
                            String cantidad = scanner.nextLine();
                            panaderiaOut.println(cantidad); // Enviar cantidad al servidor
                            String respuestaCompra = panaderiaIn.readLine();
                            System.out.println("Servidor: " + respuestaCompra);
                        } else {
                            System.out.println("Opción no válida.");
                        }
                    }

                    System.out.println("Saliendo de la panadería... ¡Hasta luego!");
                } catch (IOException e) {
                    System.err.println("Error al conectarse al servidor de panadería: " + e.getMessage());
                }
            } else {
                System.err.println("Error: " + respuesta);
            }
        } catch (IOException e) {
            System.err.println("Error de conexión al servidor de descubrimiento: " + e.getMessage());
        } finally {
            try {
                // Asegurarse de cerrar los recursos correctamente
                if (socket[0] != null && !socket[0].isClosed()) {
                    socket[0].close();
                }
                if (in[0] != null) {
                    in[0].close();
                }
                if (out[0] != null) {
                    out[0].close();
                }
            } catch (IOException e) {
                System.err.println("Error cerrando la conexión: " + e.getMessage());
            }
        }
    }
}
