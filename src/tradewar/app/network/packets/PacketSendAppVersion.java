package tradewar.app.network.packets;

import tradewar.api.IPacket;
import tradewar.api.IVersion;

public class PacketSendAppVersion implements IPacket {
	
	public static final long serialVersionUID = 27837198434264782L;
	
	
	public IVersion appVersion;

	public PacketSendAppVersion(IVersion appVersion) {
		this.appVersion = appVersion;
	}
	
	@Override
	public long getPacketId() {
		return serialVersionUID;
	}

	@Override
	public boolean check() {
		return appVersion != null;
	}
}
