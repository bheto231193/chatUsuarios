import java.net.*;
import java.io.*;
import java.lang.*;

public class Cliente{
	private Socket socketCliente;
	public static void main(String args[]){
		new Cliente(args);
	}
	public Cliente(String args[]) {
		System.out.println("Arrancando cliente");
		arrancarCliente(args);
		procesarMensajes();
	}
	private void arrancarCliente(String[] args){
		
		try{
				if(args.length==2)
					socketCliente=new Socket(args[0],Integer.parseInt(args[1]));
				else
					socketCliente=new Socket("localhost",8347);
				System.out.println("Arrancando el cliente2");
		}
		catch(java.lang.NumberFormatException e1){
			errorFatal(e1, "Numero de puerto invalido.");
		}
		catch (java.net.UnknownHostException e2) {
			errorFatal(e2, "No se localiza el ordenador servidor con ese nombre.");
		}
		catch (java.lang.SecurityException e3) {
			String mensaje ="Hay restricciones de seguridad en el servidor para conectarse por el puerto 8347";
			errorFatal(e3, mensaje);
		}
		catch (IOException e4) {
			String mensaje = "No se puede conectar con el puerto 8347 del servidor. Asegúrese de que el servidor está en marcha.";
			errorFatal(e4, mensaje);
		}
	}
	private void procesarMensajes() {
		BufferedReader entrada=null;
		PrintWriter salida=null;
		try {
			
			entrada= new BufferedReader(new
			InputStreamReader(socketCliente.getInputStream()));
			salida = new PrintWriter(socketCliente.getOutputStream(), true);
			BufferedReader entradaConsola = new BufferedReader(new
			InputStreamReader(System.in));
			
			new ThreadCliente(entrada);
			while (true)
				salida.println(entradaConsola.readLine());
		}
		catch (IOException e) {
			e.printStackTrace();
			if ( entrada != null) {
				try {
					entrada.close();
				}
				catch (Exception e1) {
					entrada = null;
				}
			}
			if ( salida != null) {
				try {
					salida.close();
				}
				catch (Exception e1) {
					salida = null;
				}
			}
			if ( socketCliente != null) {
				try {
					socketCliente.close();
				}
				catch (Exception e1) {
					socketCliente = null;
				}
			}
			String mensaje = "Se ha perdido la comunicación con el servidor. Seguramente se debe a que se ha cerrado el servidor o a errores de transmisión";
			errorFatal(e, mensaje);
		}
	}
	
	private static void errorFatal(Exception excepcion, String mensajeError) {
		excepcion.printStackTrace();
		System.out.println("Error fatal."+ System.getProperty("line.separator") +
		mensajeError+ "Información para el usuario");
		System.exit(-1);
	}
}
	
	