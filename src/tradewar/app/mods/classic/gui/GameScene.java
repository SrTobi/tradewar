package tradewar.app.mods.classic.gui;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import tradewar.api.IPacket;
import tradewar.api.IScene;
import tradewar.api.ISocket;
import tradewar.api.ISocketListener;
import tradewar.app.mods.classic.Client;
import tradewar.app.mods.classic.packets.SendChatMessagePacket;
import tradewar.app.mods.classic.packets.SendStockValuesPacket;
import tradewar.app.network.IPacketHandler;
import tradewar.app.network.PacketDistributor;
import tradewar.utils.log.Log;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.IOException;

public class GameScene extends JPanel implements IScene {

	private static final long serialVersionUID = -4570236601814288334L;

	private ISocketListener socketListener = new ISocketListener() {
		
		@Override
		public void onSend(IPacket packet) {}
		
		@Override
		public void onReceive(IPacket packet) {
			try {
				distributor.distribute(packet);
			} catch (Exception e) {
				log.excp(e);
			}
		}
		
		@Override
		public void onError(IOException e) {
		}
		
		@Override
		public void onDisconnect() {
		}
	};
	
	private IPacketHandler<SendStockValuesPacket> newStockValuesHandler = new IPacketHandler<SendStockValuesPacket>() {
		
		@Override
		public void onPacket(SendStockValuesPacket packet) throws Exception {
			economyScreen.setNewStockValues(packet.initialStockValues);
		}
		
		@Override
		public Class<SendStockValuesPacket> getPacketClass() {
			return SendStockValuesPacket.class;
		}
	};

	private Log log = new Log("game-scene");
	private PacketDistributor distributor = new PacketDistributor();
	private ISocket connection;
	private Client client;
	
	private EconomyScreen economyScreen;
	
	/**
	 * Create the panel.
	 */
	public GameScene(Client client, ISocket connection, int money, String[] stockNames, int[] stockValues) {
		
		if(client == null || connection == null || stockNames == null || stockValues == null) {
			throw new NullPointerException();
		}
		
		this.client = client;
		this.connection = connection;
		
		connection.addSocketListener(socketListener);
		distributor.addPacketHandler(newStockValuesHandler);
		
		setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.7);
		add(splitPane, BorderLayout.CENTER);
		
		economyScreen = new EconomyScreen(money, stockNames, stockValues);
		splitPane.setLeftComponent(economyScreen);
	}

	@Override
	public Component getView() {
		return this;
	}

	@Override
	public String getSceneName() {
		return "game-scene";
	}

	@Override
	public String getSceneTitle() {
		return "Tradewar";
	}

	@Override
	public void onRegister() {}
	@Override
	public void onUnregister() {}
	@Override
	public void onEnter() {}
	@Override
	public void onLeave() {}

}
