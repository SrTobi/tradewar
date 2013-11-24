package tradewar.app.network;

import java.net.ProtocolException;

import tradewar.api.ISocket;
import tradewar.api.IVersion;
import tradewar.app.network.packets.SendClientHandshakePacket;
import tradewar.app.network.packets.SendServerHandshakePacket;
import tradewar.utils.HashedPassword;

public class ServersideHandshakeProtocol extends AbstractSocketProtocol {

	private boolean accepted = false;
	private ISocket socket;
	private HashedPassword password;
	private boolean tooManyPlayer;
	private IVersion serverVersion;
	
	public ServersideHandshakeProtocol(ISocket socket, IVersion serverVersion, HashedPassword password, boolean tooManyPlayer) {
		super(socket);
		
		this.socket = socket;
		this.serverVersion = serverVersion;
		this.tooManyPlayer = tooManyPlayer;
		this.password = password;
		
		getDistributor().addPacketHandler(cliendHandshakeHandler);
	}

	
	private IPacketHandler<SendClientHandshakePacket> cliendHandshakeHandler = new IPacketHandler<SendClientHandshakePacket>() {
		
		@Override
		public void onPacket(SendClientHandshakePacket packet) throws ProtocolException {

			log.info("New player[" + packet.nickname + "] connected...");
			
			boolean passwordCorrect = password.equals(packet.hashedPassword);
			
			sendHandshakeResponse(passwordCorrect);
			
			if(passwordCorrect && !tooManyPlayer) {
				accepted = true;
			}else{
				log.info("Password is " + (passwordCorrect? "correct" : "incorrect") + "!");
				if(tooManyPlayer)
					log.info("But server is full!");
			}
			
			if(!accepted) {
				throw new ProtocolException("Player not accepted!");
			}
		}
		
		@Override
		public Class<SendClientHandshakePacket> getPacketClass() {
			return SendClientHandshakePacket.class;
		}
	};
	
	private void sendHandshakeResponse(boolean passwordCorrect) {
		SendServerHandshakePacket packet = new SendServerHandshakePacket(serverVersion, passwordCorrect, tooManyPlayer);
		
		socket.send(packet);
	}
	
	@Override
	protected boolean isProtocolCompleted() {
		return accepted;
	}
}
