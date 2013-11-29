package tradewar.app.network;

import java.net.SocketException;
import java.util.concurrent.TimeoutException;

import tradewar.api.IPacket;
import tradewar.api.ISocket;
import tradewar.app.Application;
import tradewar.utils.AbstractProtocol;

public abstract class AbstractSocketProtocol extends AbstractProtocol {

	private ISocket socket;
	private PacketDistributor distributor;
	private boolean timeout = false;
	
	public AbstractSocketProtocol(ISocket socket) {
		this.socket = socket;
		this.distributor = new PacketDistributor(Application.LOGSTREAM);
	}

	public boolean hadTimeout() {
		return timeout;
	}
	
	protected PacketDistributor getDistributor() {
		return distributor;
	}
	
	@Override
	protected void handleProtocol() throws Exception {

		handleLoop:
		while(!aborted() && !isProtocolCompleted()) {
			int timeout = getTimeout();
			
			IPacket packet = socket.waitForPacket(timeout);
			
			if(aborted()) {
				return;
			}
			
			if(packet == null) {
				int newTimeout = getTimeout();
				
				if(newTimeout <= timeout) {
					this.timeout = true;
					throw new TimeoutException("Protocol timeout! Protocol was handled to slow!");
				}
				
				continue handleLoop;
			}

			distributor.distribute(packet);
			
			if(!socket.isConnected()) {
				throw new SocketException("Disconnect during protocol execution!");
			}
		}
	}
	
	protected int getTimeout() {
		return 5000;
	}
	
	protected abstract boolean isProtocolCompleted();
}
