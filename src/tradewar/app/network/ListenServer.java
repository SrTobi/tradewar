package tradewar.app.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import tradewar.api.IListenServer;
import tradewar.api.ILogStream;
import tradewar.api.IPacket;
import tradewar.api.IServer;
import tradewar.api.IServerStartParams;
import tradewar.api.ISocket;
import tradewar.api.ISocketListener;
import tradewar.app.Application;
import tradewar.app.FormatPatterns;
import tradewar.utils.log.Log;

public class ListenServer implements IListenServer, Runnable {

	private Log log;
	private IServerStartParams ssparams;
	private IServer server;
	private ServerSocket listenSocket;
	private boolean listening = false;
	private int connectedCount = 0;
	
	public ListenServer(ILogStream logstream, IServerStartParams ssparams) throws IOException {
		this.log = new Log(logstream, "listen-server");
		this.ssparams = ssparams;
		
		listenSocket = new ServerSocket(ssparams.getGameServerPort());
	}
	
	public void setServerListener(IServer server) {
		this.server = server;
	}
	
	@Override
	public int getPlayerCount() {
		if(server == null) {
			return 0;
		}
		
		return server.getPlayerCount();
	}


	@Override
	public int getConnectedCount() {
		return connectedCount;
	}
	
	@Override
	public void listen(boolean listening) {
		
		if(server == null) {
			throw new NullPointerException("Server was not set!");
		}

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
	public void run() {
		log.info("Start acceping on port " + ssparams.getGameServerPort());

		while(isListening()) {
			try {
				Socket s = listenSocket.accept();
				
				doHandshake(s);
			} catch (IOException e) {
				log.err("Failed to accept socket!");
				log.excp(e);
			}
		}
	}
	
	private void doHandshake(Socket s) throws IOException {
		final ISocket socket = new ConnectionSocket(log.getStream(), s);
		++connectedCount;
		
		socket.addSocketListener(new ISocketListener() {
			
			@Override
			public void onSend(IPacket packet) {}
			
			@Override
			public void onReceive(IPacket packet) {}
			
			@Override
			public void onError(IOException e) {}
			
			@Override
			public void onDisconnect() {
				--connectedCount;
			}
		});

		final ServersideHandshakeProtocol handshake = createHandshakeProtocol(socket, true);
		final IProtocolListener listener = new IProtocolListener() {
			
			ServersideHandshakeProtocol protocol = handshake;
			
			@Override
			public void onProtocolFail(Exception failure) {
				if(failure instanceof SecurityException) {
					// Password was wrong... give user another chance!
					protocol = createHandshakeProtocol(socket, false);
					protocol.addProtocolListener(this);
					protocol.invokeProtocol();
				}else{
					log.excp(failure);
					socket.close();
				}
			}
			
			@Override
			public void onProtocolCompleteness() {
				// new player connected!
				
				String nickname = protocol.getPlayerName();

				if(nickname == null || !nickname.matches(FormatPatterns.NICKNAME)) {
					throw new IllegalArgumentException();
				}
				
				log.info("New player[" + nickname + "] accepted!");
				
				// infom the server!
				server.onNewPlayer(socket, nickname);
			}
			
			@Override
			public void onProtocolAbort() {
				// Hmm should not be exectued!
				assert false;
			}
		};
		handshake.addProtocolListener(listener);
		handshake.invokeProtocol();
	}
	
	private ServersideHandshakeProtocol createHandshakeProtocol(ISocket s, boolean hasTimeout) {
		boolean tooManyPlayer = connectedCount == ssparams.getMaxPlayer();
		return new ServersideHandshakeProtocol(s, Application.APP_VERSION, ssparams, tooManyPlayer, hasTimeout);
	}
}
