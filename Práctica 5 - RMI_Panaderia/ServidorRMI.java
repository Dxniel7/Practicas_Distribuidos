import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class ServidorRMI {
    public static void main(String[] args) {
        try {
            // Crear el servidor RMI
            PanaderiaServidorRMI servidor = new PanaderiaServidorRMI();

            // Crear el registro RMI en el puerto
            int puerto = 1099;
            try {
                LocateRegistry.createRegistry(puerto); 
                System.out.println("Registro RMI creado en el puerto " + puerto);
            } catch (Exception e) {
                System.out.println("El puerto " + puerto + " ya está en uso, usando otro puerto.");
                LocateRegistry.createRegistry(1098); // Cambiar al puerto 1098 si el 1099 está ocupado
            }

            // Registrar el servidor con el nombre "Panaderia"
            Naming.rebind("rmi://localhost/Panaderia", servidor);
            System.out.println("Servidor RMI de panadería iniciado...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}