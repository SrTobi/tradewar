package tradewar.app.network;

import tradewar.api.IPacket;

public interface IPacketHandler <PacketType extends IPacket> {
	public void onPacket(PacketType packet);
	
	public Class<PacketType> getPacketClass();
}
