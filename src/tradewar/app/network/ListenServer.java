package tradewar.app.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.SynchronousQueue;

import tradewar.api.IListenServer;
import tradewar.api.ILogStream;
import tradewar.api.IServer;
import tradewar.api.IServerStartParams;
import tradewar.utils.log.Log;

public class ListenServer implements IListenServer, Runnable {

	private Log log;
	private IServerStartParams ssparams;
	private IServer server;
	private ServerSocket listenSocket;
	private boolean listening;
	
	private SynchronousQueue<Object> eventQueue;
	
	
	private class Player implements Runnable {

		Socket socket;
		
		Player(Socket s) {
			this.socket = s;
			new Thread(this).start();
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
	}
	
	
	
	public ListenServer(ILogStream logstream, IServerStartParams ssparams) throws IOException {
		this.log = new Log(logstream, "listen-server");
		this.ssparams = ssparams;
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
