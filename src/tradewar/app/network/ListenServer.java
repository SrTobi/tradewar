package tradewar.app.network;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.SynchronousQueue;

import tradewar.api.IListenServer;
import tradewar.api.ILogStream;
import tradewar.api.IServer;
import tradewar.api.IServerStartParams;
import tradewar.api.ISocket;
import tradewar.app.Application;
import tradewar.utils.HashedPassword;
import tradewar.utils.log.Log;

public class ListenServer implements IListenServer, Runnable {

	private Log log;
	private IServerStartParams ssparams;
	private HashedPassword password;
	private IServer server;
	private ServerSocket listenSocket;
	private boolean listening = false;
	
	private SynchronousQueue<Object> eventQueue;
	
	
	private class Player implements Runnable {

		ISocket socket;
		
		Player(Socket s) throws IOException {
			this.socket = new ConnectionSocket(log.getStream(), s);
			new Thread(this).start();
		}
		
		@Override
		public void run() {
			
			boolean again = true;
			boolean tooManyPlayer = false;
			
			while(again) {
				try {
					new ServersideHandshakeProtocol(socket, Application.APP_VERSION, password, tooManyPlayer).executeProtocol();
					again = false;
				} catch (Exception e) {				
					// Just throw him away!
					log.excp(e);
					
					if(tooManyPlayer)
						return;
				}
			}
			
			while(true) {
				socket.waitForPacket();
			}
		}
	}
	
	
	
	public ListenServer(ILogStream logstream, IServerStartParams ssparams) throws IOException {
		this.log = new Log(logstream, "listen-server");
		this.ssparams = ssparams;
		this.password = new HashedPassword(ssparams.getServerPassword());
		eventQueue = new SynchronousQueue<Object>();
		
		listenSocket = new ServerSocket(ssparams.getGameServerPort());
	}
	
	public void setServerListener(IServer server) {
		this.server = server;
	}
	
	public int getPlayerCount() {
		if(server == null) {
			return 0;
		}
		
		return server.getPlayerCount();
	}

	@Override
	public void listen(boolean listening) {

		if(listening && !isListening()) {
			
			new Thread(this).start();
			this.listening = true;
		}else{
			this.listening = false;
		}
		
	}

	@Override
	public boolean isListening() {
		return listening;
	}

	@Override
	public void poll() {

	}

	@Override
	public void run() {
		log.info("Start acceping on port " + ssparams.getGameServerPort());

		while(isListening()) {
			try {
				Socket s = listenSocket.accept();
				
				new Player(s);
			} catch (IOException e) {
				log.err("Failed to accept socket!");
				log.excp(e);
			}
		}
		
	}
}
