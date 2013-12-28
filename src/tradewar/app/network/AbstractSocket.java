package tradewar.app.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tradewar.api.IPacket;
import tradewar.api.ISocket;
import tradewar.api.ISocketListener;
import tradewar.utils.log.Log;

public abstract class AbstractSocket implements ISocket {

	protected Log log = new Log("socket");
	private List<ISocketListener> listeners = new ArrayList<ISocketListener>();

	@Override
	public synchronized void addSocketListener(ISocketListener listener) {
		listeners.add(listener);
	}

	@Override
	public synchronized void removeSocketListener(ISocketListener listener) {
		listeners.remove(listener);
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
		
		if(!isConnected()) {
			return null;
		}
			
		try {
			wait(timeout);
		} catch (InterruptedException e) {
			log.excp(e);
		}

		return nextPacket();
	}

	protected synchronized void notifyNewPackageInList() {
		this.notify();
	}

	protected void notfiySend(IPacket packet) {
		for(ISocketListener l : getListeners())
			l.onSend(packet);
	}
	
	protected void notfiyReceive(IPacket packet) {
		for(ISocketListener l : getListeners())
			l.onReceive(packet);
		
	}
	
	protected void notfiyError(IOException e) {
		for(ISocketListener l : getListeners())
			l.onError(e);
		
	}
	
	protected void notfiyDisconnect() {
		for(ISocketListener l : getListeners())
			l.onDisconnect();		
	}
	
	protected synchronized ISocketListener[] getListeners() {
		return listeners.toArray(new ISocketListener[listeners.size()]);
	}
}
