package tradewar.app.mods.classic.client;

import java.util.ArrayList;
import java.util.List;

public class ClientModel {

	public interface IClientModelListener {
		void onMoneyChange(int dm, int money);
		void onStockValueChange(int idx, int dv, int value);
		void onStockAmountChange(int idx, int da, int amount);
		void onPlayerLevelChange(int dlvl, int lvl);
		void onUnitsChange(int idx, int du, int units);
	}
	
	
	private static final String[] levelNames = {"Imp", "Trader", "Broker", "Mogul", "Exchange god"};
	private static final int[] levelUpCosts = {50000, 500000, 2000000, 6000000 };
	private static final int[] levelSpT = {1, 2, 4, 6, 10 };
	
	static {
		if(levelNames.length != levelSpT.length || levelNames.length - 1 != levelUpCosts.length) {
			throw new IllegalStateException();
		}
	}
	
	private List<IClientModelListener> listeners = new ArrayList<ClientModel.IClientModelListener>();

	private int playerMoney;
	
	// economy
	private int playerLevelIdx;
	private String[] stockNames;
	private int[] stockValues;
	private int[] playerStockAmounts;
	
	// military
	private int shieldLevel;
	private final String[] unitNames;
	private int[] unitCosts;
	private int[] units;
	
	private final int playerId;
	private final int[] enemyIds;
	private final String[] enemyNames;
	
	public ClientModel(int money, String[] stockNames, int[] stockValues, String[] unitNames, int[] unitCosts, int[] units, int shieldLevel, int playerId, String[] playersNames) {
		if(stockNames == null || stockValues == null || unitNames == null || unitCosts == null || units == null || playersNames == null) {
			throw new NullPointerException();
		}
		
		if(stockNames.length != stockValues.length || unitNames.length != unitCosts.length || unitNames.length != units.length || playersNames.length == 0 || playerId >= playersNames.length) {
			throw new IllegalArgumentException();
		}
		
		this.playerMoney = money;
		
		this.stockNames = stockNames;
		this.stockValues = stockValues;
		this.playerLevelIdx = 0;
		this.playerStockAmounts = new int[stockNames.length];
		
		this.shieldLevel = shieldLevel;
		this.unitNames = unitNames;
		this.unitCosts = unitCosts;
		this.units = units;
		
		int plnum = playersNames.length;
		this.playerId = playerId;
		this.enemyIds = new int[plnum-1];
		this.enemyNames = new String[plnum-1];
		
		int nextIdx = 0;
		for(int i = 0; i < plnum; ++i, ++nextIdx) {
			if(i != playerId) {
				enemyIds[nextIdx] = i;
				enemyNames[nextIdx] = playersNames[i];
			}else{
				--nextIdx;
			}
		}
	}

	////////////////////////////////////////////// listener modifiers //////////////////////////////////////////////
	public void addListener(IClientModelListener listener) {
		listeners.add(listener);
	}

	public void removeListener(IClientModelListener listener) {
		listeners.remove(listener);
	}
	
	////////////////////////////////////////////// dynamic infos //////////////////////////////////////////////
	public int getPlayerMoney() {
		return playerMoney;
	}

	public int[] getStockValues() {
		return stockValues;
	}

	public int[] getStockAmounts() {
		return playerStockAmounts;
	}
	
	public int getMaxStockBuyOrder(int idx) {
		if(idx < 0 || idx >= getStockNum()) {
			throw new IllegalArgumentException();
		}
		
		int value = getStockValues()[idx];
		
		if(getPlayerMoney() >= 0) {
			return Math.min(getStocksPerTrade(), Math.max(1, getPlayerMoney() / value));
		} else {
			return 0;
		}
	}
	
	public boolean canBuyStocks(int idx) {
		if(idx < 0 || idx >= getStockNum()) {
			throw new IllegalArgumentException();
		}
		
		return getPlayerMoney() >= 0;
	}
	
	public int getMaxStockSellOrder(int idx) {
		if(idx < 0 || idx >= getStockNum()) {
			throw new IllegalArgumentException();
		}
		
		return Math.min(getStocksPerTrade(), getStockAmounts()[idx]);
	}
	
	public boolean canSellStocks(int idx) {
		if(idx < 0 || idx >= getStockNum()) {
			throw new IllegalArgumentException();
		}
		
		return getStockAmounts()[idx] > 0;
	}
	
	public int getPlayerLevel() {
		return playerLevelIdx + 1;
	}
	
	public String getLevelIdentifier() {
		return levelNames[playerLevelIdx];
	}
	
	public int getStocksPerTrade() {
		return levelSpT[playerLevelIdx];
	}
	
	public int getLevelUpCosts() {
		if(!canUpgradePlayerLevel()) {
			throw new IllegalStateException();
		}
		
		return levelUpCosts[playerLevelIdx];
	}
	
	public int getLevelUpBonus() {
		if(!canUpgradePlayerLevel()) {
			throw new IllegalStateException();
		}
		
		return levelSpT[playerLevelIdx + 1] - levelSpT[playerLevelIdx];
	}
	
	public boolean canUpgradePlayerLevel() {
		return getPlayerLevel() < getMaxLevel();
	}
	

	public int[] getUnitCosts() {
		return unitCosts;
	}
	
	public int[] getUnits() {
		return units;
	}
	
	public int getShieldLevel() {
		return shieldLevel;
	}
	
	public int getShieldUpgradeCosts() {
		return (int) Math.pow(2, getShieldLevel()) * 5 + getShieldLevel() * 5000;
	}

	////////////////////////////////////////////// static infos //////////////////////////////////////////////
	
	public int getStockNum() {
		return stockNames.length;
	}
	
	public String[] getStockNames() {
		return stockNames;
	}
	
	public int getMaxLevel() {
		return levelNames.length;
	}
	
	public int getUnitNum() {
		return unitNames.length;
	}
	
	public String[] getUnitNames() {
		return unitNames;
	}
	
	public int getPlayerId() {
		return playerId;
	}
	
	public String[] getEnemyNames() {
		return enemyNames;
	}

	////////////////////////////////////////////// modifiers //////////////////////////////////////////////
	public void setMoney(int money) {
		if(playerMoney != money) {
			int dm = money - playerMoney;
			playerMoney = money;
			fireMoneyUpdate(dm);
		}
	}
	
	public int addMoney(int dm) {
		
		setMoney(getPlayerMoney() + dm);
		return playerMoney;
	}

	public void setStockValues(int[] stockValues) {
		int[] oldValues = this.stockValues;
		this.stockValues = stockValues;
		for(int i = 0; i < getStockNum(); ++i) {
			if(oldValues[i] != stockValues[i])
				fireStockValueUpdate(i, stockValues[i] - oldValues[i]);
		}
	}
	
	public int buyStocks(int idx) {
		if(idx < 0 || idx >= getStockNum()) {
			throw new IllegalArgumentException();
		}
		
		if(!canBuyStocks(idx)) {
			return 0;
		}

		int value = getStockValues()[idx];
		int orderSize = getMaxStockBuyOrder(idx);

		playerStockAmounts[idx] += orderSize;
		addMoney(-value * orderSize);
		
		fireStockAmountUpdate(idx, orderSize);
		
		return orderSize;
	}

	
	public int sellStocks(int idx) {
		if(idx < 0 || idx >= getStockNum()) {
			throw new IllegalArgumentException();
		}
		
		if(!canSellStocks(idx)) {
			return 0;
		}

		int value = getStockValues()[idx];
		int orderSize = getMaxStockSellOrder(idx);

		playerStockAmounts[idx] -= orderSize;
		addMoney(value * orderSize);
		
		fireStockAmountUpdate(idx, -orderSize);
		
		return orderSize;
	}

	public boolean upgradePlayerLevel() {
		if(!canUpgradePlayerLevel()) {
			throw new IllegalStateException();
		}
		
		int costs = getLevelUpCosts();
		if(getPlayerMoney() < costs) {
			return false;
		}

		++playerLevelIdx;
		addMoney(-costs);
		
		firePlayerLevelUpdate(1 /* you can only upgrade one lave at a time*/);
		
		return true;
	}
	
	public int buyUnits(int idx, int amount) {
		if(idx >= units.length || amount < 0) {
			throw new IllegalArgumentException();
		}
		
		int unit_cost = getUnitCosts()[idx];
		amount = Math.min(getPlayerMoney() / unit_cost, amount);
		
		
		if(amount > 0) {
			int costs = unit_cost * amount;
			
			units[idx] += amount;
			addMoney(-costs);
			
			fireUnitsUpdate(idx, amount);
		}else{
			amount = 0;
		}
		
		return amount;
	}
	
	////////////////////////////////////////////// listeners //////////////////////////////////////////////
	private void fireMoneyUpdate(int dm) {
		for(IClientModelListener listener : listeners) {
			listener.onMoneyChange(dm, getPlayerMoney());
		}
	}

	private void fireStockValueUpdate(int idx, int dv) {
		for(IClientModelListener listener : listeners) {
			listener.onStockValueChange(idx, dv, getStockValues()[idx]);
		}
	}

	private void fireStockAmountUpdate(int idx, int da) {
		for(IClientModelListener listener : listeners) {
			listener.onStockAmountChange(idx, da, getStockAmounts()[idx]);
		}
	}

	private void firePlayerLevelUpdate(int dlvl) {
		for(IClientModelListener listener : listeners) {
			listener.onPlayerLevelChange(dlvl, getPlayerLevel());
		}
	}
	
	private void fireUnitsUpdate(int idx, int du) {
		for(IClientModelListener listener : listeners) {
			listener.onUnitsChange(idx, du, getUnits()[idx]);
		}
	}
}
