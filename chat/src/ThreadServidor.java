import java.io.*;
import java.util.*;
import javax.swing.*;
import java.net.*;

class ThreadServidor extends Thread {
	private String nombreCliente; 
	private static String historial = "C:" + File.separatorChar + "java3-a/historial.txt";//la ruta del archivo historial.txt
	private static List clientesActivos = new ArrayList(); 
	private Socket socket;
	private BufferedReader entrada;
	private PrintWriter salida;
	public ThreadServidor(Socket socket) throws IOException {
		this.socket = socket;
		PrintWriter salidaArchivo = null;

		salida = new PrintWriter(socket.getOutputStream(), true);

		entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		escribirHistorial("Conexión desde la dirección");

		start();
	}
	public void run() {
		String textoUsuario = "";
		String mensaje="";
		String destino="";
		try {
			salida.println("> Bienvenido a este chat.");
			salida.println("> Introduzca su nombre, por favor.");
			nombreCliente = (entrada.readLine()).trim();
			if ( (nombreCliente.equals("")) || (nombreCliente == null) ) {
				nombreCliente = "Invitado";
			}
			Iterator it = clientesActivos.iterator();
			while (it.hasNext()) {
				if (nombreCliente.equals(( (ThreadServidor) it.next()).nombreCliente)) {
					nombreCliente = nombreCliente + socket.getPort();
					break;
				}
			}

			anyadirConexion(this);
			salida.println("> Se le asignado el alias de " + nombreCliente);
			salida.println("> Introduzca salir o desconectar para terminar la comunicacion.");
			salida.println("> Introduzca lista o listado para ver los clientes conectados.");
			salida.println("> Introduzca todos o broadcast + enter y escriba el mensaje + enter para mandar mensaje a todos los clientes.");
			salida.println("> Introduzca privado o unico + enter y escriba el mensaje + enter para mandar mensaje a un solo cliente.");

			socket.setSoTimeout(Servidor.TIEMPO_DESCONEXION_AUTOMATICA);
			
			while ( (textoUsuario=entrada.readLine()) != null ) {
				mensaje="";
				if ((textoUsuario.equals("salir"))|| (textoUsuario.equals("desconectar"))) {
					salida.println("> ***********HASTA LA VISTA****************");
					escribirHistorial("Desconexión voluntaria desde la dirección:");
					break;
				}
				else if ((textoUsuario.equals("lista"))||(textoUsuario.equals("listado"))) {
					escribirCliente(this,"> " + listarClientesActivos());
				}
				else if ((textoUsuario.equals("privado"))||(textoUsuario.equals("unico"))){
					escribirCliente(this,"> " + listarClientesActivos());
					salida.println("> Escribe el destinatario:");
					destino=entrada.readLine().trim();
					salida.println("> Escribe el mensaje:");
					mensaje=entrada.readLine();
					if(mensaje!=null){
						escribirPrivado(destino, nombreCliente+"> "+ mensaje);
					}
					else{
						escribirPrivado(destino, nombreCliente+"> vacio");
					}

				}
				else if ((textoUsuario.equals("broadcast"))||(textoUsuario.equals("todos"))){
					mensaje=entrada.readLine();
					if(mensaje!=null){
						escribirATodos(nombreCliente+"> "+ mensaje);
					}
					else{
						escribirATodos(nombreCliente+"> Hola");
					}

				}
				else {
					escribirATodos(nombreCliente+"> "+ textoUsuario);
				}
			} 
		} 
		catch (java.io.InterruptedIOException e1) {
			escribirCliente(this, "> "+ "***************************************");
			escribirCliente(this, "> "+ "Se pasó del tiempo límite: Conexión cerrada");
			escribirCliente(this, "> "+ "Si desea continuar, abra otra sesión");
			escribirCliente(this, "> "+ "*****************ADIOS*****************");
			escribirHistorial("Desconexión por fin de tiempo desde la dirección:");
		}
		catch (IOException e2) {
			escribirHistorial("Desconexión involuntaria desde la dirección:");
		}
		finally {
			eliminarConexion(this);
			limpiar();
		} 
	}

	private void limpiar() {
		
		if ( entrada != null ) {
			try {
				entrada.close();
			}
			catch (IOException e1) {}
			entrada = null;
		}
		if ( salida != null ) {
			salida.close();
			salida = null;
		}
		if ( socket != null ) {
			try {
				socket.close();
			}
			catch (IOException e2) {}
			socket = null;
		}
	}
	
	private static synchronized void eliminarConexion(ThreadServidor threadServidor) {
		clientesActivos.remove(threadServidor);
	}
	
	private static synchronized void anyadirConexion(ThreadServidor threadServidor) {
		clientesActivos.add(threadServidor);
	}
	
	private synchronized void escribirATodos(String textoUsuario) {
		Iterator it = clientesActivos.iterator();
		while (it.hasNext()) {
			ThreadServidor tmp = (ThreadServidor) it.next();
			if ( !(tmp.equals(this)) )
				escribirCliente(tmp, textoUsuario);
		}
	}
	
	private synchronized void escribirCliente(ThreadServidor threadServidor, String textoUsuario) {
		(threadServidor.salida).println(textoUsuario);
	}
	
	private synchronized void escribirPrivado(String destino, String textoUsuario) {
		if ( (destino.equals("")) || (destino == null) ) {
			destino = "nombreCliente";
		}
		else{	
			salida.println("el destino es:"+destino);
			salida.println("el mensaje es:"+textoUsuario);		

			Iterator ite = clientesActivos.iterator();
			while (ite.hasNext()) {
				ThreadServidor tmp2;
				if (destino.equals((tmp2=(ThreadServidor)ite.next()).nombreCliente)) {
					
					escribirCliente(tmp2, textoUsuario);
					
				}
			
			}
		}
	}
	
	private static synchronized StringBuffer listarClientesActivos() {
		StringBuffer cadena = new StringBuffer();
		for (int i = 0; i < clientesActivos.size(); i++) {
			ThreadServidor tmp = (ThreadServidor) (clientesActivos.get(i));
			cadena.append((((ThreadServidor) clientesActivos.get(i)).nombreCliente)).append("||") ;
		}
		return cadena;
	}
	
	private synchronized void escribirHistorial(String mensaje ) {
		PrintWriter salidaArchivo = null;
		try {
			salidaArchivo = new PrintWriter(new BufferedWriter (new FileWriter(historial, true))); // true = autoflush
			salidaArchivo.println(mensaje + " " +socket.getInetAddress().getHostName() +" por el puerto " + socket.getPort() +
					" en la fecha " + new Date());
		}
		catch (IOException e1) {
			System.out.println( "Fallo en el archivo de historial.");
		}
		finally {
			if (salidaArchivo != null) {
				salidaArchivo.close();
				salidaArchivo = null;
			}
		}
	}
}