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

public class ConnectionSocket implements ISocket {

	private Log log;
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private Queue<IPacket> receivedPackets;
	private ReceiverThread receiverThread;
	private List<ISocketListener> listeners;
	
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
							ConnectionSocket.this.notify();
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
		
		this.log = new Log(logstream, "socket");
		this.receivedPackets = new ConcurrentLinkedQueue<IPacket>();
		this.listeners = new ArrayList<ISocketListener>();
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
	public synchronized void addSocketListener(ISocketListener listener) {
		listeners.add(listener);
	}

	@Override
	public synchronized void removeSocketListener(ISocketListener listener) {
		listeners.remove(listener);
	}

	@Override
	public synchronized void send(IPacket packet) throws IOException {
		notfiySend(packet);
		out.writeObject(packet);
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
	public synchronized IPacket waitForPacket() {

		while(true) {
			
			IPacket result = nextPacket();
			if(result != null) {
				return result;
			}
			
			try {
				wait();
			} catch (InterruptedException e) {
				log.excp(e);
			}
		}

	}

	@Override
	public synchronized IPacket waitForPacket(int timeout) {

		IPacket result = nextPacket();
		if(result != null) {
			return result;
		}
			
		try {
			wait(timeout);
		} catch (InterruptedException e) {
			log.excp(e);
		}

		return nextPacket();
	}	

	@Override
	public void close() {
		receiverThread.stop();
		
		if(!isConnected())
			return;
		
		try {
			socket.close();
		} catch (IOException e) {
			log.excp(e);
		}
		
		notfiyDisconnect();
		socket = null;
	}
	
	private boolean isConnected() {
		return socket != null;
	}

	private void notfiySend(IPacket packet) {
		for(ISocketListener l : listeners)
			l.onSend(packet);
	}
	
	private void notfiyReceive(IPacket packet) {
		for(ISocketListener l : listeners)
			l.onReceive(packet);
		
	}
	
	private void notfiyError(IOException e) {
		for(ISocketListener l : listeners)
			l.onError(e);
		
	}
	
	private void notfiyDisconnect() {
		for(ISocketListener l : listeners)
			l.onDisconnect();		
	}
}