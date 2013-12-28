package tradewar.app.network;

import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.naming.OperationNotSupportedException;

import tradewar.api.IPacket;
import tradewar.api.ISocket;

public class LocalSocket extends AbstractSocket {


	private Queue<IPacket> receivedPackets = new ConcurrentLinkedQueue<IPacket>();
	private LocalSocket peer;
	
	private LocalSocket() {
		
	}
	
	
	@Override
	public Socket getUnderlyingSocket() throws OperationNotSupportedException {
		throw new OperationNotSupportedException();
	}

	@Override
	public boolean isConnected() {
		return peer != null;
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
		
		if(isConnected()) {
			peer.receive(packet);
		}else{
			notfiyError(new IOException("Socket is not connected!"));
		}
	}

	private void receive(IPacket packet) {
		receivedPackets.add(packet);
		notifyNewPackageInList();
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
		if(isConnected()) {
			shutdown();
			peer.shutdown();
		}
	}
	
	private synchronized void shutdown() {
		peer = null;
		notfiyDisconnect();
	}
	
	
	public static ISocket[] createLocalSocketPair() {
		
		LocalSocket fst = new LocalSocket();
		LocalSocket snd = new LocalSocket();
		fst.peer = snd;
		snd.peer = fst;
		
		return new ISocket[] {fst, snd};
	}
}
