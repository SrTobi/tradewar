package tradewar.app.network;

import java.net.ProtocolException;

import tradewar.api.ISocket;
import tradewar.api.IVersion;
import tradewar.app.network.packets.SendClientHandshakePacket;
import tradewar.app.network.packets.SendServerHandshakePacket;

public class ClientsideHandshakeProtocol extends AbstractSocketProtocol {

	private ISocket socket;
	private IVersion serverVersion = null;
	private boolean passwordIncorrect = false;
	private boolean tooManyPlayer = false;
	private boolean accepted = false;
	
	
	public ClientsideHandshakeProtocol(ISocket socket, String nickname, String password) {
		super(socket);
		
		this.socket = socket;


		PacketDistributor d = getDistributor();
		
		d.addPacketHandler(appVersionHandler);
		
		sendCliendHandshake(nickname, password);
	}
	
	public IVersion getServerVersion() {
		return serverVersion;
	}
	
	public boolean isPasswordIncorrect() {
		return passwordIncorrect;
	}
	
	public boolean isServesrFull() {
		return tooManyPlayer;
	}
	
	private IPacketHandler<SendServerHandshakePacket> appVersionHandler = new IPacketHandler<SendServerHandshakePacket>() {
		
		@Override
		public void onPacket(SendServerHandshakePacket packet) throws ProtocolException {

			serverVersion = packet.serverAppVersion;

			if(!packet.passwordCorrect) {
				passwordIncorrect = true;
			}

			if(packet.tooManyPlayer) {
				tooManyPlayer = true;
			}
			
			accepted = packet.accepted;
			
			if(!accepted) {
				throw new ProtocolException("Handshake with server failed!");
			}
		}
		
		/*private void checkVersion(IVersion srvVersion) {
			
			if(srvVersion.isNewerThen(Application.APP_VERSION)) {
				// New version available!
				throw new NotImplementedError();
			}
			
			if(!Application.APP_VERSION.isCompatible(srvVersion)) {
				// Versions are not compatible
				throw new NotImplementedError();
			}
		}*/
		
		@Override
		public Class<SendServerHandshakePacket> getPacketClass() {
			return SendServerHandshakePacket.class;
		}
	};
	
	private void sendCliendHandshake(String nickname, String password) {
		SendClientHandshakePacket packet = new SendClientHandshakePacket(nickname, password);
		
		socket.send(packet);
	}

	@Override
	protected boolean isProtocolCompleted() {
		
		return accepted;
	}
	
	
}
