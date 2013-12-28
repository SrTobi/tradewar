package tradewar.app.mods.classic.packets;

import tradewar.api.IPacket;

public class SendAttackingPackage implements IPacket {

	private static final long serialVersionUID = -2624530838306863826L;

	public final int playerId;
	public final boolean attack;
	
	public SendAttackingPackage(int playerId, boolean attack) {
		this.playerId = playerId;
		this.attack = attack;
	}
	
	@Override
	public long getPacketId() {
		return serialVersionUID;
	}

	@Override
	public boolean check() {
		return playerId >= 0;
	}

}
