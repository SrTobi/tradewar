package tradewar.app.mods.classic.client;

import java.io.IOException;

import javax.swing.SwingUtilities;

import tradewar.api.IPacket;
import tradewar.api.ISocket;
import tradewar.api.ISocketListener;
import tradewar.app.mods.classic.client.ClientModel.IClientModelListener;
import tradewar.app.mods.classic.packets.SendArmyEnlargedPacket;
import tradewar.app.mods.classic.packets.SendStockValuesPacket;
import tradewar.app.network.IPacketHandler;
import tradewar.app.network.PacketDistributor;
import tradewar.utils.log.Log;

public class ClientNetworkBinding implements ISocketListener, IClientModelListener {


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
		model.addListener(this);
	}
	

	/////////////////////////////////////////////////// Socket Listener ///////////////////////////////////////////////////
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
	public void onSend(IPacket packet) {}

	@Override
	public void onError(IOException e) {}

	@Override
	public void onDisconnect() {}


	/////////////////////////////////////////////////// Model Listener ///////////////////////////////////////////////////
	@Override
	public void onMoneyChange(int dm, int money) {}


	@Override
	public void onStockValueChange(int idx, int dv, int value) {}


	@Override
	public void onStockAmountChange(int idx, int da, int amount) {}


	@Override
	public void onPlayerLevelChange(int dlvl, int lvl) {}


	@Override
	public void onUnitsChange(int idx, int du, int units) {
		if(du > 0) {
			connection.send(new SendArmyEnlargedPacket(idx, du));
		}
	}
	
	/////////////////////////////////////////////////// Handler ///////////////////////////////////////////////////
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
}
