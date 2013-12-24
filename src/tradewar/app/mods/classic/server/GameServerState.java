package tradewar.app.mods.classic.server;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import tradewar.api.IPacket;
import tradewar.api.ISocket;
import tradewar.api.ISocketListener;
import tradewar.app.mods.classic.packets.SendGameInitPackage;
import tradewar.app.mods.classic.packets.SendStockValuesPacket;
import tradewar.app.network.PacketDistributor;
import tradewar.utils.log.Log;

public class GameServerState implements IServerState{

	private static final int INITIAL_MONEY = 2000;
	
	private class Player {
		
		private String nickname;
		private ISocket connection;
		private PacketDistributor distributor = new PacketDistributor();
		
		private ISocketListener listener = new ISocketListener() {
			
			@Override
			public void onSend(IPacket packet) {}
			
			@Override
			public void onReceive(final IPacket packet) {
				executor.execute(new Runnable() {
						
					@Override
					public void run() {
						try {
							distributor.distribute(packet);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
			
			@Override
			public void onError(IOException e) {}
			
			@Override
			public void onDisconnect() {}
		};
		
		Player(String nickname, ISocket connection) {
			this.nickname = nickname;
			this.connection = connection;
			
			connection.addSocketListener(listener);
		}
		
		public String getNickname() {
			return nickname;
		}
		
		public void send(IPacket packet) {
			connection.send(packet);
		}
		
		public ISocket unbind() {
			connection.removeSocketListener(listener);
			return connection;
		}
		
	}

	private Executor executor = Executors.newFixedThreadPool(1);
	private Timer timer = new Timer();
	private Log log = new Log("game-server");
	private Player[] players;
	private Random random = new Random();
	
	// Stocks
	private final String[] stockNames = {"Tobi Corp.", "Gold Mining Corp.", "Moon Inc.", "Dieu Société", "Unit-Test AG"};
	private int[] stockValues;
	
	// Units
	private final String[] unitNames = {"Missile tank", "Soldier", "F 22", "Anti aircraft gun", "Cruiser", "Submarine"};
	private int[] unitCosts = {1500, 2000, 1500, 2000, 1500, 2000};
	
	public GameServerState(ISocket[] connections, String[] nicknames) {
		
		if(connections.length != nicknames.length) {
			throw new IllegalArgumentException();
		}
		
		// Create Players
		int plynum = connections.length;
		
		players = new Player[plynum];
		
		for(int i = 0; i < plynum; ++i) {
			players[i] = new Player(nicknames[i], connections[i]);
		}
		
		
		// Build stocks
		stockValues = new int[stockNames.length];
		
		for(int i = 0; i < stockNames.length; ++i) {
			stockValues[i] = 1000;
		}
		
		// start...
		sendGameServerInitMessage();
		
		// start stock changing
		refreshStockValues(300);
	}

	@Override
	public int getPlayerCount() {
		return 0;
	}

	@Override
	public void stop() {
	}

	@Override
	public void onTextCommand(String cmd) {
	}

	@Override
	public void onNewPlayer(ISocket socket, String nickname) {
		socket.close();
	}
	
	private void sendGameServerInitMessage() {
		execute(new Runnable() {
			
			@Override
			public void run() {
				String[] nicknames = new String[players.length];
				
				for(int i = 0; i < players.length; ++i) {
					nicknames[i] = players[i].getNickname();
				}
				
				int[] units = new int[unitNames.length];
				
				Arrays.fill(units, 0);
				
				IPacket packet = new SendGameInitPackage(nicknames, INITIAL_MONEY, stockNames, stockValues, unitNames, units, unitCosts, 0);
				
				sendToAll(packet);
			}
		});
	}
	
	private void refreshStockValues(int milliseconds) {
		
		execute(new Runnable() {
			
			@Override
			public void run() {
				
				stockValues = generateNewStock(stockValues);
				
				// send new stock values
				IPacket packet = new SendStockValuesPacket(stockValues);
				sendToAll(packet);
				
				// setup new stock value generation
				refreshStockValues(rand(800, 1500));
			}
		}, milliseconds);
		
	}
	
	private int[] generateNewStock(int[] stock) {
		
		int[] newStock = new int[stock.length];
		
		for(int i = 0; i < stock.length; ++i) {
			newStock[i] = stock[i];
			if(random.nextDouble() < (2.0/5.0)) {
				newStock[i] += rand(-2500, 2500) - ((stock[i] > 5500)? rand(500, 1200) : 0);
				
				if( newStock[i] <= 0 )
					newStock[i] = rand(20, 220);
			}
		}
		
		return newStock;
	}
	
	private int rand(int from, int to) {
		return from + random.nextInt(to - from);
	}
	
	private void sendToAll(IPacket packet) {
		
		for(Player player : players) {
			player.send(packet);
		}
	}
	
	private void execute(Runnable r) {
		executor.execute(r);
	}
	
	private void execute(final Runnable r, int milliseconds) {
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				execute(r);
			}
		}, milliseconds);
	}
}
