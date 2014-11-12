package design;

import java.net.ServerSocket;
import java.net.Socket;

public class CommServer extends Thread{
	// wait for connection, start thread
	boolean connected = false;
	Socket socket;
	
	@Override
	public void run() {
		while (!connected){
			ServerSocket s;
		}
	}
}
