package tradewar.app.network;

import java.net.ProtocolException;

import tradewar.api.IServerStartParams;
import tradewar.api.ISocket;
import tradewar.api.IVersion;
import tradewar.app.network.packets.SendClientHandshakePacket;
import tradewar.app.network.packets.SendServerHandshakePacket;
import tradewar.utils.Hasher;

public class ServersideHandshakeProtocol extends AbstractSocketProtocol {

	private boolean accepted = false;
	private ISocket socket;
	private boolean tooManyPlayer;
	private String playerName;
	private IVersion serverVersion;
	private IServerStartParams ssparams;
	private boolean hasTimeout;
	
	public ServersideHandshakeProtocol(ISocket socket, IVersion serverVersion, IServerStartParams ssparams, boolean tooManyPlayer, boolean hasTimeout) {
		super(socket);
		
		this.socket = socket;
		this.serverVersion = serverVersion;
		this.tooManyPlayer = tooManyPlayer;
		this.ssparams = ssparams;
		this.hasTimeout = hasTimeout;
		
		getDistributor().addPacketHandler(cliendHandshakeHandler);
	}
	
	public String getPlayerName() {
		return  playerName;
	}

	
	private IPacketHandler<SendClientHandshakePacket> cliendHandshakeHandler = new IPacketHandler<SendClientHandshakePacket>() {
		
		@Override
		public void onPacket(SendClientHandshakePacket packet) throws ProtocolException {

			log.info("Player[" + packet.nickname + "] tries to login...");

			boolean passwordCorrect = ssparams.getHashedServerPassword() == null
						|| (packet.hashedPassword != null && Hasher.isEqual(ssparams.getHashedServerPassword(), packet.hashedPassword));
			
			sendHandshakeResponse(passwordCorrect);
			
			if(passwordCorrect && !tooManyPlayer) {
				accepted = true;
			}else{
				log.info("Password is " + (passwordCorrect? "correct" : "incorrect") + "!");
				if(tooManyPlayer)
					log.info("But server is full!");
			}
			
			if(!passwordCorrect) {
				throw new SecurityException("Wrong password!");
			}
			
			if(!accepted) {
				throw new ProtocolException("Player not accepted!");
			}
			
			playerName = packet.nickname;
		}
		
		@Override
		public Class<SendClientHandshakePacket> getPacketClass() {
			return SendClientHandshakePacket.class;
		}
	};
	
	private void sendHandshakeResponse(boolean passwordCorrect) {
		SendServerHandshakePacket packet = new SendServerHandshakePacket(serverVersion, passwordCorrect, tooManyPlayer, ssparams);
		
		socket.send(packet);
	}
	
	@Override
	protected int getTimeout() {
		if(hasTimeout) {
			return super.getTimeout();
		}else {
			return 999999999;
		}
	}
	
	
	@Override
	protected boolean isProtocolCompleted() {
		return accepted;
	}
}
