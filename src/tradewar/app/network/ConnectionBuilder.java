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
	private int timeout = 5000;
	
	private IVersion appVersion;
	private int remainingProtocolPackets = 3;
	
	
	public ConnectionBuilder(ILogStream logstream, IVersion appVersion, String addr, int port) {
		this.log = new Log(logstream);
		this.address = addr;
		this.port = port;
		this.appVersion = appVersion;
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
			
	
			if(connecting) {
				log.info("Connection established! Wait for handshake...");
				handleHandshake();
			}
	
		} catch(IOException e) {
			if(isConnecting()) {
				failed = true;
				onFailed(e);
			}
		} finally {

			connecting = false;
			if(!failed) {
				if(isConnected()) {
					onConnected(socket);
				} else {
					onCanceled();
				}
			}
			
		}
	}
	
	private void handleHandshake() {
		PacketDistributor distributor = createDistributor();
		
		while(remainingProtocolPackets > 0) {
			IPacket packet = socket.waitForPacket(timeout);
			
			if(packet == null) {
				// timeout
				throw new NotImplementedError();
			}
			
			distributor.distribute(packet);
		}
		
		onConnected(socket);
	}
	
	private PacketDistributor createDistributor() {

		PacketDistributor distributor = new PacketDistributor(log.getStream());
		
		
		/*
		 * Handler for version sending
		 */
		distributor.addPacketHandler(new IPacketHandler<PacketSendAppVersion>() {

			private boolean handeled = false;
			
			@Override
			public void onPacket(PacketSendAppVersion packet) {
				
				if(handeled) {
					log.warn("Received 'PacketSendAppVersion' multiple times!");
					return;
				}
				handeled = true;
				
				if(packet.appVersion.isNewerThen(appVersion)) {
					// ask the user if he wants to update!
					throw new NotImplementedError();
				}
				
				if(!appVersion.isCompatible(packet.appVersion)) {
					// versions are not compatible!
					throw new NotImplementedError();
				}
				
				--remainingProtocolPackets;
			}
			@Override
			public Class<PacketSendAppVersion> getPacketClass() {
				return PacketSendAppVersion.class;
			}
		});
		
		return distributor;
	}

	public abstract void onConnected(ISocket socket);
	public abstract void onCanceled();
	public abstract void onFailed(IOException e);
}
