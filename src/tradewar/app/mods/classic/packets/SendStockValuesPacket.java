package tradewar.app.mods.classic.packets;

import tradewar.api.IPacket;

public class SendStockValuesPacket implements IPacket {

	private static final long serialVersionUID = 3569430807365931885L;

	public final int[] stockValues;
		
	public SendStockValuesPacket(int[] stockValues) {

		this.stockValues = stockValues;
	}
		
	@Override
	public long getPacketId() {
		return serialVersionUID;
	}

	@Override
	public boolean check() {	
		//TODO: check array values!
		return stockValues != null;
	}
}
