package tradewar.app.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.SynchronousQueue;

import tradewar.api.ILogStream;
import tradewar.api.IPacket;
import tradewar.api.ISocket;
import tradewar.utils.log.Log;

public class ConnectionSocket implements ISocket {

	private Log log;
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private Queue<IPacket> receivedPackets;
	private ReceiverThread receiverThread;
	
	private class ReceiverThread implements Runnable {

		private boolean receiving = true;
		
		public ReceiverThread() {
			new Thread(this).start();
		}
		
		@Override
		public void run() {
			
			while(receiving) {
				try {
					Object obj = in.readObject();
					
					if(!(obj instanceof IPacket)) {
						throw new ClassNotFoundException("A class was received, but is not a packet!");
					}
					
					IPacket packet = (IPacket)obj;
					receivedPackets.add(packet);
					
				} catch (ClassNotFoundException | IOException e) {
					log.warn("Failed to read incomming packet!");
					log.excp(e);
				}
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
		this.receivedPackets = new SynchronousQueue<IPacket>();
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
	public void send(IPacket packet) throws IOException {
		out.writeObject(packet);
	}

	@Override
	public IPacket receive() {
		if(receivedPackets.isEmpty()) {
			return null;
		} else {
			return receivedPackets.remove();
		}
	}

	@Override
	public void close() {
		receiverThread.stop();
		try {
			socket.close();
		} catch (IOException e) {
		}
	}	
}
