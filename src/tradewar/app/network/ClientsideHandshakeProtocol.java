package tradewar.app.network;

import tradewar.api.ISocket;
import tradewar.app.Application;
import tradewar.app.network.packets.PacketSendAppVersion;

public class ClientsideHandshakeProtocol extends AbstractProtocol<ClientsideHandshakeProtocol> {

	private ISocket socket;
	private static final int FEATURE_APP_VERSION = 0;
	
	boolean[] protocolFeatures = new boolean[1];
	
	
	public ClientsideHandshakeProtocol(ISocket socket) {
		super(socket);
		
		this.socket = socket;
		
		for(int i = 0; i < protocolFeatures.length; ++i) {
			protocolFeatures[i] = false;
		}
		
		getDistributor().addPacketHandler(appVersionHandler);
		
		startProtocol();
	}
	
	private IPacketHandler<PacketSendAppVersion> appVersionHandler = new IPacketHandler<PacketSendAppVersion>() {
		
		@Override
		public void onPacket(PacketSendAppVersion packet) {
			
			if(protocolFeatures[FEATURE_APP_VERSION])
				return;
			
			if(packet.appVersion.isNewerThen(Application.APP_VERSION)) {
				// New version available!
			}
			
			if(!Application.APP_VERSION.isCompatible(packet.appVersion)) {
				// Versions are not compatible
			}
			
			protocolFeatures[FEATURE_APP_VERSION] = true;
		}
		
		@Override
		public Class<PacketSendAppVersion> getPacketClass() {
			return PacketSendAppVersion.class;
		}
	};
	

	@Override
	protected boolean protocolFinished() {

		for(int i = 0; i < protocolFeatures.length; ++i) {
			if(!protocolFeatures[i])
				return false;
		}
		
		return true;
	}
	
	
}
