package tradewar.app.mods.classic.packets;

import tradewar.api.IPacket;

public class SendArmyEnlargedPacket implements IPacket {

	private static final long serialVersionUID = 6957000339477335826L;

	public final int unitIdx;
	public final int unitAmount;
	
	public SendArmyEnlargedPacket(int unitIdx, int amount) {
		this.unitIdx = unitIdx;
		this.unitAmount = amount;
	}
	
	@Override
	public long getPacketId() {
		return serialVersionUID;
	}

	@Override
	public boolean check() {
		return unitIdx >= 0 && unitAmount > 0;
	}

}
