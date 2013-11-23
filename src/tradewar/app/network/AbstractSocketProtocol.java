package tradewar.app.network;

import java.net.SocketException;
import java.util.concurrent.TimeoutException;

import tradewar.api.IPacket;
import tradewar.api.ISocket;
import tradewar.app.Application;

public abstract class AbstractSocketProtocol extends AbstractProtocol {

	private ISocket socket;
	private PacketDistributor distributor;
	
	public AbstractSocketProtocol(ISocket socket) {
		this.socket = socket;
		this.distributor = new PacketDistributor(Application.LOGSTREAM);
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
