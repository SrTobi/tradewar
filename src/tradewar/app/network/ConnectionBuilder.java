package tradewar.app.network;

import java.io.IOException;
import java.net.Socket;

import tradewar.api.ILogStream;
import tradewar.api.IPacket;
import tradewar.api.ISocket;
import tradewar.api.IVersion;
import tradewar.app.network.packets.PacketSendAppVersion;
import tradewar.utils.NotImplementedError;
import tradewar.utils.log.Log;

public abstract class ConnectionBuilder implements Runnable {
	
	private Log log;
	private String address;
	private int port;
	private ISocket socket;
	private boolean connecting = false;
	private boolean connected = false;
	
	
	public ConnectionBuilder(ILogStream logstream, String addr, int port) {
		this.log = new Log(logstream);
		this.address = addr;
		this.port = port;
	}
	
	public ISocket getConnection() {
		return socket;
	}
	
	public boolean isConnected() {
		return connected;
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
		boolean failed = false;
		try {
			log.info("Connecting to " + address + ":" + port);
			socket = new ConnectionSocket(log.getStream(), new Socket(address, port));
			
		} catch(IOException e) {
			if(isConnecting()) {
				failed = true;
				onFailed(e);
			}
		} finally {

			connecting = false;
			if(!failed) {
				if(isConnected()) {
					log.info("Connection established! Wait for handshake...");
					onConnected(socket);
				} else {
					onCanceled();
				}
			}		
		}
	}

	public abstract void onConnected(ISocket socket);
	public abstract void onCanceled();
	public abstract void onFailed(IOException e);
}
