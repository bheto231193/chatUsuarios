import java.io.*;
import java.util.*;
import javax.swing.*;
import java.net.*;

public class Servidor
{ 
	protected static final int TIEMPO_DESCONEXION_AUTOMATICA = 600000; 
        
	private ServerSocket socketServidor;
	public static void main(String args[]){
		new Servidor();
	}
	public Servidor() {
		System.out.println("Arrancando el servidor por el puerto 8347");
		arrancarServidor();
		procesarClientes();
	}
	private void arrancarServidor(){
		
		try{
			socketServidor = new ServerSocket(8347);
			System.out.println("El servidor está en marcha: escucha por el puerto 8347");
		}
		catch (java.net.BindException e1) {
			String mensaje = "No puede arrancarse el servidor por el puerto 8347. Seguramente, el puerto está ocupado.";
			errorFatal(e1, mensaje);
		}
		catch (java.lang.SecurityException e2) {
			String mensaje = "No puede arrancarse el servidor por el puerto 8347. Seguramente, hay restricciones de seguridad.";
			errorFatal(e2, mensaje);
		}
		catch (IOException e3) {
			String mensaje = "No puede arrancarse el servidor por el puerto 8347";
			errorFatal(e3, mensaje);
		}	
	}
	private void procesarClientes() {
	
		Socket socketCliente = null;
		while (true) {
			try {
				socketCliente = socketServidor.accept(); 
				System.out.println("Conexión del cliente con dirección"+socketCliente.getInetAddress().getHostAddress() +" por el puerto "+socketCliente.getPort());
				try {
					new ThreadServidor(socketCliente); 
				}
				catch (IOException e1) {
					if (socketCliente != null) {
						try {
							socketCliente.close();
						}
						catch (IOException e2) {} 
					}
				}
			}
			catch (java.lang.SecurityException e3) {
				if (socketServidor != null) {
					try {	
						socketServidor.close();
					}
					catch (IOException e4) {} 
				}
				String mensaje = "Con su configuración de seguridad, los clientes no pueden conectarse por el puerto 8347";
				errorFatal(e3, mensaje);
			}
			catch (IOException e5) {
			} 
		} 
	}

	private static void errorFatal(Exception excepcion, String mensajeError) {
		excepcion.printStackTrace();
		System.out.println("Error fatal."+ System.getProperty("line.separator") +
		mensajeError+ "Información para el usuario");
		System.exit(-1);
	}
}
