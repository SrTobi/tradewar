package tradewar.app.mods.classic.packets;

import tradewar.api.IPacket;

public class SendWarResultPacket implements IPacket {

	private static final long serialVersionUID = -4292285430419375662L;

	public final int attackerId;
	public final int defenderId;
	public final boolean won;
	public final int loot;
	public final int lostLife;
	public final int[] lostUnits;
	
	public SendWarResultPacket(int attId, int defId, boolean won, int loot, int lostLife, int[] lostUnits) {
		this.attackerId = attId;
		this.defenderId = defId;
		this.won = won;
		this.loot = loot;
		this.lostLife = lostLife;
		this.lostUnits = lostUnits;
	}
	
	
	@Override
	public long getPacketId() {
		return serialVersionUID;
	}

	@Override
	public boolean check() {
		// TODO: check array values
		return loot >= 0 && lostLife >= 0 && lostUnits != null && attackerId != defenderId;
	}

}
