package tradewar.app.mods.classic.packets;

import tradewar.api.IPacket;

public class SendPlayerEliminatedPacket implements IPacket {

	
	private static final long serialVersionUID = -1648207475717787478L;

	public final int playerId;
	public final String reason;
	
	public SendPlayerEliminatedPacket(int playerId, String reason) {
		this.playerId = playerId;
		this.reason = reason;
	}
	
	@Override
	public long getPacketId() {
		return serialVersionUID;
	}

	@Override
	public boolean check() {
		return playerId >= 0 && reason != null;
	}

}
