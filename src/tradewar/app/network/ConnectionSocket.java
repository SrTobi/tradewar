package tradewar.app.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import tradewar.api.ILogStream;
import tradewar.api.IPacket;
import tradewar.api.ISocket;
import tradewar.api.ISocketListener;
import tradewar.utils.log.Log;

public class ConnectionSocket extends AbstractSocket {

	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private Queue<IPacket> receivedPackets;
	private ReceiverThread receiverThread;
	private boolean connected = true;
	
	private class ReceiverThread implements Runnable {

		private boolean receiving = true;
		
		public ReceiverThread() {
			new Thread(this).start();
		}
		
		@Override
		public void run() {
			
			try {
				while(receiving) {
					try {
						Object obj = in.readObject();
						
						if (obj == null) {
							return;
						}
						
						if(!(obj instanceof IPacket)) {
							log.err("A class was received, but is not a packet!");
							continue;
						}
						
						IPacket packet = (IPacket)obj;
						
						if(packet.check()) {
							notfiyReceive(packet);
							receivedPackets.add(packet);
							
							notifyNewPackageInList();
						}else{
							log.err("Packet contained invalid data!");
						}
						
						
					} catch (ClassNotFoundException e) {
						log.warn("Failed to read incomming packet!");
						log.excp(e);
					}
				}
			} catch(IOException e) {
				log.excp(e);
				if(connected) {
					notfiyError(e);
				}
				
			} finally {
				close();
			}
		}
		
		public void stop() {
			receiving = false;
		}
	}
	
	public ConnectionSocket(ILogStream logstream, Socket socket) throws IOException {
		
		if(!socket.isConnected() || socket.isClosed()) {
			throw new IllegalArgumentException("Can not handle a closed socket!");
		}
		
		this.receivedPackets = new ConcurrentLinkedQueue<IPacket>();
		this.socket = socket;
		this.out = new ObjectOutputStream(socket.getOutputStream());
		out.flush();
		this.in = new ObjectInputStream(socket.getInputStream());
		this.receiverThread = new ReceiverThread();
	}
	
	@Override
	public Socket getUnderlyingSocket() {
		return socket;
	}

	@Override
	public synchronized void send(IPacket packet) {
		if(packet == null) {
			throw new NullPointerException("packet must not be null!");
		}
		
		if(!packet.check()) {
			throw new IllegalArgumentException("Packet is not valid!");
		}
		
		notfiySend(packet);
		try {
			out.writeObject(packet);
		} catch (IOException e) {
			notfiyError(e);
			close();
		}
	}

	@Override
	public boolean hasNewPacket() {
		return !receivedPackets.isEmpty();
	}

	@Override
	public synchronized IPacket nextPacket() {
		if(hasNewPacket()) {
			return receivedPackets.remove();
		} else {
			return null;
		}
	}


	@Override
	public synchronized void close() {
		receiverThread.stop();
		
		if(!isConnected())
			return;
		
		connected = false;
		
		try {
			socket.close();
		} catch (IOException e) {
			log.excp(e);
		}

		socket = null;
		notfiyDisconnect();
	}
	
	@Override
	public boolean isConnected() {
		return socket != null && connected;
	}
}
