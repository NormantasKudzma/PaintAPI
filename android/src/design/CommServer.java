package design;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class CommServer extends Thread{
	public static final int PORT = 60210;
	
	Socket socket;
	BufferedReader reader;
	PrintWriter writer;
	AndroidDesign design;

	boolean connected = false;
	boolean done = false;
	
	public CommServer(AndroidDesign design){
		this.design = design;
	}
	
	@Override
	public void run() {
		try {
			ServerSocket server = new ServerSocket(PORT);					
			server.setReuseAddress(true);
			System.out.println("Socket is now accepting connections");
			socket = server.accept();				
			System.out.println("Connected " + socket.getInetAddress());
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream());
			server.close();
			
			while (!done){
				String command = reader.readLine();
				design.processAction(command);
				writer.println(design.getImageBytes());
				System.out.println("Before flush..");
				writer.flush();
				System.out.println("Sent some stuff over.");
				Thread.sleep(50);
			}
			stopServer();
		}
		catch (Exception e){
			stopServer();
			e.printStackTrace();
		}
	}
	
	public void stopServer(){
		try {
			System.out.println("Server stopped");
			done = true;
			if (writer != null) writer.close();
			if (reader != null) reader.close();
			if (socket != null && !socket.isClosed()) socket.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
}
