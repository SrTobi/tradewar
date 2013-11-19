package tradewar.app.network;

import java.io.IOException;
import java.net.Socket;

public abstract class ConnectionBuilder implements Runnable {
	
	private String address;
	private int port;
	private Socket socket;
	private boolean connecting;
	
	
	public ConnectionBuilder(String addr, int port) {
		this.address = addr;
		this.port = port;
	}
	
	public Socket getConnection() {
		return socket;
	}
	
	public boolean isConnected() {
		return socket != null;
	}
	
	public boolean isConnecting() {
		return connecting;
	}
	
	public void connect() {
		
		if(!isConnecting()) {
			connecting = true;
			new Thread(this).start();
		}
	}
	
	public void cancel() {
		connecting = false;
	}

	@Override
	public void run() {
		try {
			socket = new Socket(address, port);
		} catch (IOException e) {
			if(connecting)
				onFailed(e);
		}

		if(connecting) {
			onConnected(socket);
		}else{
			onCanceled();
		}

		connecting = false;
	}
	
	public abstract void onConnected(Socket socket);
	public abstract void onCanceled();
	public abstract void onFailed(IOException e);
}
