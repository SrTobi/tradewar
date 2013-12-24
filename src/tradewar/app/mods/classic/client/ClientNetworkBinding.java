package tradewar.app.mods.classic.client;

import java.io.IOException;

import javax.swing.SwingUtilities;

import tradewar.api.IPacket;
import tradewar.api.ISocket;
import tradewar.api.ISocketListener;
import tradewar.app.mods.classic.packets.SendStockValuesPacket;
import tradewar.app.network.IPacketHandler;
import tradewar.app.network.PacketDistributor;
import tradewar.utils.log.Log;

public class ClientNetworkBinding implements ISocketListener {

	private IPacketHandler<SendStockValuesPacket> newStockValuesHandler = new IPacketHandler<SendStockValuesPacket>() {
		
		@Override
		public void onPacket(SendStockValuesPacket packet) throws Exception {
			model.setStockValues(packet.stockValues);
		}
		
		@Override
		public Class<SendStockValuesPacket> getPacketClass() {
			return SendStockValuesPacket.class;
		}
	};

	private Log log = new Log("cl-net-binding");
	private PacketDistributor distributor = new PacketDistributor();
	private ClientModel model;
	private ISocket connection;
	
	public ClientNetworkBinding(ClientModel model, ISocket connection) {
		if(model == null || connection == null) {
			throw new NullPointerException();
		}
		
		this.model = model;
		this.connection = connection;
		
		distributor.addPacketHandler(newStockValuesHandler);
		
		connection.addSocketListener(this);
	}
	
	
	@Override
	public void onReceive(final IPacket packet) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				try {
					distributor.distribute(packet);
				} catch (Exception e) {
					log.excp(e);
				}
			}
		});
	}

	@Override
	public void onSend(IPacket packet) {
	}

	@Override
	public void onError(IOException e) {
	}

	@Override
	public void onDisconnect() {
	}

}
