package tradewar.app.mods.classic;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import tradewar.api.IPacket;
import tradewar.api.ISceneFrame;
import tradewar.api.IServer;
import tradewar.api.ISocket;
import tradewar.api.ISocketListener;
import tradewar.app.mods.classic.packets.SendChatMessagePacket;
import tradewar.app.network.IPacketHandler;
import tradewar.app.network.PacketDistributor;
import tradewar.utils.log.Log;

public class LobbyServer implements IServer {

	private class Player {
		
		private String nickname;
		private ISocket connection;
		private PacketDistributor distributor = new PacketDistributor();
		
		private ISocketListener listener = new ISocketListener() {
			
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
			public void onError(IOException e) {}
			
			@Override
			public void onDisconnect() {}
		};
		
		Player(String nickname, ISocket connection) {
			this.nickname = nickname;
			this.connection = connection;
			
			connection.addSocketListener(listener);
			distributor.addPacketHandler(chatMessageHandler);
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
		
		private IPacketHandler<SendChatMessagePacket> chatMessageHandler = new IPacketHandler<SendChatMessagePacket>() {
			
			@Override
			public void onPacket(SendChatMessagePacket packet) throws Exception {
				sendChatMessageToAll(getNickname(), packet.message);
			}
			
			@Override
			public Class<SendChatMessagePacket> getPacketClass() {
				return SendChatMessagePacket.class;
			}
		};
	}
	
	Log log = new Log("lobby-server");
	PrintStream output;
	List<Player> playerList = new LinkedList<>();
	
	@Override
	public int getPlayerCount() {
		return 0;
	}
	
	
	@Override
	public void start(PrintStream terminal, ISceneFrame frame) {
		output = terminal;
		
		output.println("Server created!");
		try {
			output.println("Local ipv4 is " + Inet4Address.getLocalHost().getHostAddress());
			output.println("Local ipv6 is " + Inet6Address.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			output.println("Local ip is unknown!");
		}
	}

	@Override
	public void stop() {
	}

	@Override
	public void onNewPlayer(ISocket socket, String nickname) {
		sendServerMessageToAll("New player: " + nickname);
		playerList.add(new Player(nickname, socket));
	}


	@Override
	public void onTextCommand(String cmd) {
		
		if(cmd.startsWith("/")) {
			onCommand(cmd);
		}else{
			sendServerMessageToAll(cmd);
		}
	}
	
	public void sendChatMessageToAll(String sender, String msg) {
		output.println("[" + sender + "]: " + msg);
		
		SendChatMessagePacket packet = new SendChatMessagePacket(false, sender, msg);
		sendToAll(packet);
	}
	
	private void sendServerMessageToAll(String message) {
		output.println("server: " + message);

		SendChatMessagePacket packet = new SendChatMessagePacket(true, "server", message);
		sendToAll(packet);
	}

	private void sendToAll(IPacket packet) {
		for(Player player : playerList) {
			player.send(packet);
		}
	}

	private void onCommand(String cmd) {

		String[] parts = cmd.split(" ");
		
		if(parts.length < 1) {
			return;
		}
		
		switch(parts[0]) {
		case "/help":
			output.println("Available commands:");
			output.println("/help");
			output.println("/start");
			output.println("/list");
			output.println("/kick");
			break;
			
		case "/start":
			startServer();
			break;
			
		default:
			output.println("Unknown command!");
		}
	}


	private void startServer() {
		
	}

}
