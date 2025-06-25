import java.net.InetAddress;
import java.rmi.Naming;
import java.util.Scanner;

public class ClienteRMI {
    public static void main(String[] args) {
        try {
            // Conexión con el servidor RMI
            PanaderiaRMI panaderia = (PanaderiaRMI) Naming.lookup("rmi://localhost/Panaderia");
            System.out.println("Conexión exitosa con el servidor RMI");

            Scanner scanner = new Scanner(System.in);

            // Solicitar el nombre del cliente
            System.out.print("Por favor, ingrese su nombre: ");
            String nombreCliente = scanner.nextLine();
            String ipCliente = InetAddress.getLocalHost().getHostAddress();

            // Notificar la conexión al servidor
            panaderia.registrarConexion(nombreCliente, ipCliente);
            System.out.println("¡Bienvenido, " + nombreCliente + "!");

            while (true) {
                System.out.println("\n--- Menú Panadería ---");
                System.out.println("1. Ver stock");
                System.out.println("2. Comprar pan");
                System.out.println("3. Salir");
                System.out.print("Seleccione una opción: ");

                int opcion = scanner.nextInt();

                if (opcion == 1) {
                    System.out.println("Stock disponible: " + panaderia.obtenerStock() + " panes.");
                } else if (opcion == 2) {
                    System.out.print("Ingrese la cantidad de pan que desea comprar: ");
                    int cantidad = scanner.nextInt();
                    boolean compraExitosa = panaderia.comprarPan(nombreCliente, cantidad);
                    if (compraExitosa) {
                        System.out.println("Compra exitosa, " + nombreCliente + ". Panes restantes: " + panaderia.obtenerStock());
                    } else {
                        System.out.println("Lo sentimos, " + nombreCliente + ". No hay suficiente pan. Por favor, espere mientras horneamos más.");
                    }
                } else if (opcion == 3) {
                    System.out.println("Gracias por visitar la panadería, " + nombreCliente + ". ¡Hasta luego!");
                    // Notificar la desconexión al servidor
                    panaderia.registrarDesconexion(nombreCliente);
                    break;
                } else {
                    System.out.println("Opción no válida. Intente de nuevo.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error al conectar con el servidor RMI:");
            e.printStackTrace();
        }
    }
}