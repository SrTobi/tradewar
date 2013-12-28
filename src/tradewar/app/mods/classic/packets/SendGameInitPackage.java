package tradewar.app.mods.classic.packets;

import tradewar.api.IPacket;

public final class SendGameInitPackage implements IPacket {

	private static final long serialVersionUID = -2632772215660735089L;

	public final String[] players;
	public final int startMoney;
	public final int initialLife;
	
	public final String[] stockNames;
	public final int[] initialStockValues;
	
	public final String[] unitNames;
	public final int[] initialUnits;
	public final int[] initialUnitCosts;
	public final int initialShieldLevel;

	public final int playerId;

	
	
		
	public SendGameInitPackage(int playerId, String[] players, int life, int startMoney, String[] stockNames, int[] initialStockValues, String[] unitNames,
								int[] initialUnits, int[] initialUnitCosts, int initialShieldLevel) {

		this.playerId = playerId;
		this.initialLife = life;
		this.players = players;
		this.startMoney = startMoney;

		this.stockNames = stockNames;
		this.initialStockValues = initialStockValues;

		this.unitNames = unitNames;
		this.initialUnits = initialUnits;
		this.initialUnitCosts = initialUnitCosts;
		this.initialShieldLevel = initialShieldLevel;
	}
		
	@Override
	public long getPacketId() {
		return serialVersionUID;
	}

	@Override
	public boolean check() {	
		//TODO: check array values!
		return players != null
			&& stockNames != null && initialStockValues != null
			&& stockNames.length == initialStockValues.length
			
			&& unitNames != null && initialUnits != null && initialUnitCosts != null
			&& unitNames.length == initialUnits.length
			&& unitNames.length == initialUnitCosts.length;
	}
}
