package tradewar.app.network;

import tradewar.api.IPacket;

public interface IPacketHandler <PacketType extends IPacket> {
	public void onPacket(PacketType packet) throws Exception;
	
	public Class<PacketType> getPacketClass();
}
