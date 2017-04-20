import java.io.*;
import java.util.*;
import javax.swing.*;
import java.net.*;
class ThreadCliente extends Thread {
	private BufferedReader entrada;
	public ThreadCliente (BufferedReader entrada) throws IOException {
		this.entrada=entrada;
		start(); 
	}
	public void run() {
		
		String fin1 = "> *****************ADIOS*****************";
	
		String fin2 = "> ***********HASTA LA VISTA****************";
		String linea = null;
		try {
			while( ( linea=entrada.readLine() ) != null ) {
				System.out.println(linea);
				if ( linea.equals(fin1) || linea.equals(fin2) )
					break;
			}
		}	
		catch (IOException e1) {
			e1.printStackTrace();
		}
		finally {
			if (entrada !=null) {
				try {
					entrada.close();
				}
				catch (IOException e2) {} 
			}
			System.exit(-1);
		}
	} 
}

