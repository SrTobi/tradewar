package tradewar.app.mods.classic.server;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import tradewar.api.IPacket;
import tradewar.api.ISocket;
import tradewar.api.ISocketListener;
import tradewar.app.mods.classic.packets.SendArmyEnlargedPacket;
import tradewar.app.mods.classic.packets.SendAttackingPackage;
import tradewar.app.mods.classic.packets.SendGameInitPackage;
import tradewar.app.mods.classic.packets.SendPlayerEliminatedPacket;
import tradewar.app.mods.classic.packets.SendStockValuesPacket;
import tradewar.app.mods.classic.packets.SendWarResultPacket;
import tradewar.app.network.IPacketHandler;
import tradewar.app.network.PacketDistributor;
import tradewar.utils.log.Log;

public class GameServerState implements IServerState{

	private static final int INITIAL_MONEY = 2000;
	private static final int INITIAL_LIFE = 5000;
	private static final int INITIAL_SHIELD_LVL = 1;
	
	private static final int INITIAL_PEACE_TIME =45000;
	
	private class Player {
		
		private final int playerId;
		private String nickname;
		private int playerLife;
		private int[] units;
		private Set<Integer> attacking = new HashSet<Integer>();
		
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
			public void onDisconnect() {
				if(playerLife > 0) {
					sendToAll(new SendPlayerEliminatedPacket(getPlayerId(), "connection lost"));
				}
			}
		};
		
		Player(int playerId, String nickname, ISocket connection, int life, int[] units) {
			this.playerLife = life;
			this.playerId = playerId;
			this.nickname = nickname;
			this.units = units.clone();
			this.connection = connection;
			
			connection.addSocketListener(listener);
			distributor.addPacketHandler(armyEnlargedHandler);
			distributor.addPacketHandler(attackingHandler);
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

		public int[] getUnits() {
			return units;
		}
		
		private IPacketHandler<SendArmyEnlargedPacket> armyEnlargedHandler = new IPacketHandler<SendArmyEnlargedPacket>() {
			
			@Override
			public void onPacket(SendArmyEnlargedPacket packet) throws Exception {
				if(packet.unitIdx >= units.length) {
					throw new IllegalArgumentException();
				}
				
				units[packet.unitIdx] += packet.unitAmount;
			}
			
			@Override
			public Class<SendArmyEnlargedPacket> getPacketClass() {
				return SendArmyEnlargedPacket.class;
			}
		};
		
		private IPacketHandler<SendAttackingPackage> attackingHandler = new IPacketHandler<SendAttackingPackage>() {
			
			@Override
			public void onPacket(SendAttackingPackage packet) throws Exception {
				int enemyId = packet.playerId;
				if(enemyId != playerId && enemyId < players.length) {
					Player enemy = players[enemyId];
					
					if(enemy != null) {
						boolean changed = false;
						if(packet.attack) {
							changed = attacking.add(enemyId);
						}else{
							changed = attacking.remove(enemyId);
						}
						
						if(changed) {
							enemy.send(new SendAttackingPackage(playerId, packet.attack));
						}
					}else{
						attacking.remove(enemyId);
					}
				}
			}
			
			@Override
			public Class<SendAttackingPackage> getPacketClass() {
				return SendAttackingPackage.class;
			}
		};

		public void doAttacks() {
			for(int i : attacking) {
				Player p = players[i];
				if(p != null) {
					calculateWar(this, p);
				}
			}
		}
		
		public void giveWarResults(int attackerId, int defenderId, int[] lostUnits, int loot, int lostLife, boolean won) {
			for(int i = 0; i < units.length; ++i) {
				units[i] -= Math.min(units[i], lostUnits[i]);
			}
			
			if(!won) {
				playerLife -= lostLife;
			}
			
			send(new SendWarResultPacket(attackerId, defenderId, won, loot, lostLife, lostUnits));
			
			if(playerLife <= 0) {
				sendToAll(new SendPlayerEliminatedPacket(getPlayerId(), "no more lifes"));
				connection.close();
			}
		}

		public int getPlayerId() {
			return playerId;
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
		

		int[] units = new int[unitNames.length];
		Arrays.fill(units, 0);
		
		for(int i = 0; i < plynum; ++i) {
			players[i] = new Player(i, nicknames[i], connections[i], INITIAL_LIFE, units);
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
		performWar(INITIAL_PEACE_TIME);
	}

	@Override
	public int getPlayerCount() {
		return players.length;
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
				
				
				for(int i = 0; i < players.length; ++i) {
					IPacket packet = new SendGameInitPackage(i, nicknames, INITIAL_LIFE, INITIAL_MONEY, stockNames, stockValues, unitNames, players[i].getUnits(), unitCosts, INITIAL_SHIELD_LVL);
					players[i].send(packet);
				}
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
	
	private void performWar(int milliseconds) {
		execute(new Runnable() {
			
			@Override
			public void run() {
				for(int i = 0; i < players.length; ++i) {
					Player p = players[i];
					if(p != null) {
						p.doAttacks();
					}
				}
				
				performWar(rand(1000, 4000));
			}
		}, milliseconds);
	}
	
	private void calculateWar(Player attacker, Player defender) {
		int attackerUnits = sum(attacker.getUnits());
		int defenderUnits = sum(defender.getUnits());
		
		int[] attackerLost = new int[unitNames.length];
		Arrays.fill(attackerLost, defenderUnits / 5);

		int[] defenderLost = new int[unitNames.length];
		Arrays.fill(defenderLost, attackerUnits / 6);
	
		boolean attackerWon = attackerUnits > defenderUnits;
		int loot = rand(0, attackerWon? 50000 : 10000);
		int lostLife = (!attackerWon)? 0 : rand(50, 500);
		attacker.giveWarResults(attacker.getPlayerId(), defender.getPlayerId(), attackerLost, loot, lostLife, attackerWon);
		defender.giveWarResults(attacker.getPlayerId(), defender.getPlayerId(), defenderLost, loot, lostLife, !attackerWon);
	}
	
	private int sum(int[] a) {
		int res = 0;
		for(int i : a) {
			res += i;
		}
		return res;
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
