import java.rmi.Remote;
import java.rmi.RemoteException;

// Interfaz remota para la panadería
// Esta interfaz define los métodos que pueden ser invocados remotamente
public interface PanaderiaRMI extends Remote {
    int obtenerStock() throws RemoteException; // Método para obtener el stock de panes
    boolean comprarPan(String nombreCliente, int cantidad) throws RemoteException; // Método para comprar panes
    void añadirStock(int cantidad) throws RemoteException; //método para añadir stock de panes
    void registrarConexion(String nombreCliente, String ipCliente) throws RemoteException; // Método para registrar la conexión del cliente
    void registrarDesconexion(String nombreCliente) throws RemoteException; // Método para registrar la desconexión del cliente
}