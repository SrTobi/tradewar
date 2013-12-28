package tradewar.app.mods.classic.client;

import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import tradewar.api.IPacket;
import tradewar.api.ISocket;
import tradewar.api.ISocketListener;
import tradewar.app.mods.classic.client.ClientModel.IClientModelListener;
import tradewar.app.mods.classic.packets.SendArmyEnlargedPacket;
import tradewar.app.mods.classic.packets.SendAttackingPackage;
import tradewar.app.mods.classic.packets.SendPlayerEliminatedPacket;
import tradewar.app.mods.classic.packets.SendStockValuesPacket;
import tradewar.app.mods.classic.packets.SendWarResultPacket;
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
		distributor.addPacketHandler(attackingHandler);
		distributor.addPacketHandler(warResultHandler);
		distributor.addPacketHandler(playerEliminationHandler);
		
		
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
	public void onDisconnect() {
		JOptionPane.showMessageDialog(null, "Disconnected!");
		System.exit(1);

	}


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
	
	@Override
	public void onEnemyStatusChange(int idx, int id, boolean attackChange, boolean attack, boolean defendChange, boolean defend, boolean alive) {
		if(attackChange) {
			connection.send(new SendAttackingPackage(id, attack));
		}
	}

	@Override
	public void onWar(int idx, boolean wasAttacking, int dlife, boolean won) {}
	
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

	private IPacketHandler<SendAttackingPackage> attackingHandler = new IPacketHandler<SendAttackingPackage>() {
		
		@Override
		public void onPacket(SendAttackingPackage packet) throws Exception {
			if(packet.playerId == model.getPlayerId()) {
				// We are attacking us?
			}else{
				model.setAttackedBy(model.getEnemyIndexByPlayerId(packet.playerId), packet.attack);
			}
		}
		
		@Override
		public Class<SendAttackingPackage> getPacketClass() {
			return SendAttackingPackage.class;
		}
	};

	private IPacketHandler<SendWarResultPacket> warResultHandler = new IPacketHandler<SendWarResultPacket>() {
		
		@Override
		public void onPacket(SendWarResultPacket packet) throws Exception {
			
			int enemyId;
			boolean attacking = packet.attackerId == model.getPlayerId();
			
			if(attacking) {
				enemyId = model.getEnemyIndexByPlayerId(packet.defenderId);
			}else{
				enemyId = model.getEnemyIndexByPlayerId(packet.attackerId);
			}
			
			if(enemyId == -1) {
				throw new IllegalArgumentException();
			}
				
			model.applyWar(enemyId, attacking, packet.won, packet.lostUnits, packet.loot, packet.lostLife);
		}
		
		@Override
		public Class<SendWarResultPacket> getPacketClass() {
			return SendWarResultPacket.class;
		}
	};

	private IPacketHandler<SendPlayerEliminatedPacket> playerEliminationHandler = new IPacketHandler<SendPlayerEliminatedPacket>() {
		
		@Override
		public void onPacket(SendPlayerEliminatedPacket packet) throws Exception {
			
			if(packet.playerId == model.getPlayerId()) {

				JOptionPane.showMessageDialog(null, "You lost!");
				System.exit(0);
			}
			
			model.setEnemyDeath(packet.playerId, packet.reason);

			if(model.hasWon()) {
				JOptionPane.showMessageDialog(null, "You won!");
				System.exit(0);
			}
		}
		
		@Override
		public Class<SendPlayerEliminatedPacket> getPacketClass() {
			return SendPlayerEliminatedPacket.class;
		}
	};
}
