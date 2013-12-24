package tradewar.app.mods.classic.client;

import java.util.ArrayList;
import java.util.List;

public class ClientModel {

	public interface IClientModelListener {
		void onMoneyChange(int dm, int money);
		void onStockValueChange(int idx, int dv, int value);
		void onStockAmountChange(int idx, int da, int amount);
		void onPlayerLevelChange(int dlvl, int lvl);
	}
	
	
	private static final String[] levelNames = {"Imp", "Trader", "Broker", "Mogul", "Exchange god"};
	private static final int[] levelUpCosts = {50000, 500000, 2000000, 6000000 };
	private static final int[] levelSpT = {1, 2, 4, 6, 10 };
	
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
	
	public ClientModel(int money, String[] stockNames, int[] stockValues, String[] unitNames, int[] unitCosts, int[] units, int shieldLevel) {
		this.playerMoney = money;
		
		this.stockNames = stockNames;
		this.stockValues = stockValues;
		this.playerLevelIdx = 0;
		this.playerStockAmounts = new int[stockNames.length];
		
		this.shieldLevel = shieldLevel;
		this.unitNames = unitNames;
		this.unitCosts = unitCosts;
		this.units = units;
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
		if(!canIncreaseLevel()) {
			throw new IllegalStateException();
		}
		
		return levelUpCosts[playerLevelIdx];
	}
	
	public int getLevelUpBonus() {
		if(!canIncreaseLevel()) {
			throw new IllegalStateException();
		}
		
		return levelSpT[playerLevelIdx + 1] - levelSpT[playerLevelIdx];
	}
	
	public boolean canIncreaseLevel() {
		return getPlayerLevel() < getMaxLevel();
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
		
		addMoney(-value * orderSize);
		playerStockAmounts[idx] += orderSize;
		
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
		
		addMoney(value * orderSize);
		playerStockAmounts[idx] -= orderSize;
		
		fireStockAmountUpdate(idx, -orderSize);
		
		return orderSize;
	}

	public boolean upgradePlayerLevel() {
		if(!canIncreaseLevel()) {
			throw new IllegalStateException();
		}
		
		int costs = getLevelUpCosts();
		if(getPlayerMoney() < costs) {
			return false;
		}
		
		addMoney(-costs);
		++playerLevelIdx;
		
		firePlayerLevelUpdate(1 /* you can only upgrade one lave at a time*/);
		
		return true;
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
}
