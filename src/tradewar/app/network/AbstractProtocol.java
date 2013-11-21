package tradewar.app.network;

import java.util.ArrayList;
import java.util.List;

import tradewar.api.IPacket;
import tradewar.api.ISocket;
import tradewar.app.Application;

public abstract class AbstractProtocol<Protocol> implements Runnable {

	private ISocket socket;
	private PacketDistributor distributor;
	private List<IProtocolListener<Protocol>> listeners = new ArrayList<>();
	private int timeout = 5000;
	
	public AbstractProtocol(ISocket socket) {
		this.socket = socket;
		this.distributor = new PacketDistributor(Application.LOGSTREAM);
	}
	
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	public void addProtocolListener(IProtocolListener<Protocol> listener) {
		listeners.add(listener);
	}

	public void removeProtocolListener(IProtocolListener<Protocol> listener) {
		listeners.remove(listener);
	}
	
	protected void notifyProtocolFail(Protocol protocol) {
		for(IProtocolListener<Protocol> listener : listeners) {
			listener.onProtocolFail(protocol);
		}
	}

	protected void notifyProtocolComplete(Protocol protocol) {
		for(IProtocolListener<Protocol> listener : listeners) {
			listener.onProtocolCompleted(protocol);
		}
	}
	
	protected void startProtocol() {
		new Thread(this).start();
	}
	
	protected PacketDistributor getDistributor() {
		return distributor;
	}

	@Override
	public void run() {
		
		while(!protocolFinished()) {
			
			IPacket packet = socket.waitForPacket(timeout);
			
			if(packet == null) {
				break;
			}
			
			distributor.distribute(packet);
		}
		
	}
	
	protected abstract boolean protocolFinished();
}
