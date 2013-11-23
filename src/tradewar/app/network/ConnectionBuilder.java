package tradewar.app.network;

import java.io.IOException;
import java.net.Socket;

import tradewar.api.ISocket;

public class ConnectionBuilder extends AbstractProtocol {
	
	private String address;
	private int port;
	private ISocket socket;
	
	
	public ConnectionBuilder(String addr, int port) {
		this.address = addr;
		this.port = port;
	}
	
	public ISocket getConnection() {
		return socket;
	}

	@Override
	protected void handleProtocol() throws IOException {
		
		log.info("Connecting to " + address + ":" + port);
		socket = new ConnectionSocket(log.getStream(), new Socket(address, port));
		log.info("Connection established! Wait for handshake...");
	}
}
