package tradewar.app.mods.classic.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientModel {

	public interface IClientModelListener {
		void onMoneyChange(int dm, int money);
		void onStockValueChange(int idx, int dv, int value);
		void onStockAmountChange(int idx, int da, int amount);
		void onPlayerLevelChange(int dlvl, int lvl);
		void onUnitsChange(int idx, int du, int units);
		void onEnemyStatusChange(int idx, int id, boolean attackChange, boolean attack, boolean defendChange, boolean defend, boolean alive);
		void onWar(int idx, boolean wasAttacking, int dlife, boolean won);
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
	private int playerLife;
	private int shieldLevel;
	private final String[] unitNames;
	private int[] unitCosts;
	private int[] units;
	
	private final int playerId;
	private final int[] enemyIds;
	private final String[] enemyNames;
	private final boolean[] enemyAlive;
	private final boolean[] enemysWeAttack;
	private final boolean[] enemysWhoAttackUs;
	
	public ClientModel(int money, String[] stockNames, int[] stockValues, int life, String[] unitNames, int[] unitCosts, int[] units, int shieldLevel, int playerId, String[] playersNames) {
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
		
		this.playerLife = life;
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
		enemyAlive = new boolean[enemyNames.length];
		Arrays.fill(enemyAlive, true);
		enemysWeAttack = new boolean[enemyNames.length];
		Arrays.fill(enemysWeAttack, false);
		enemysWhoAttackUs = new boolean[enemyNames.length];
		Arrays.fill(enemysWhoAttackUs, false);
	}

	////////////////////////////////////////////// listener modifiers //////////////////////////////////////////////
	public synchronized void addListener(IClientModelListener listener) {
		listeners.add(listener);
	}

	public synchronized void removeListener(IClientModelListener listener) {
		listeners.remove(listener);
	}
	
	////////////////////////////////////////////// dynamic infos //////////////////////////////////////////////
	public int getPlayerMoney() {
		return playerMoney;
	}


	public int getPlayerLife() {
		return playerLife;
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

	public boolean isEnemyAlive(int idx) {
		if(idx < 0 || idx >= getEnemyCount()) {
			throw new IllegalArgumentException();
		}
		
		return enemyAlive[idx];
	}
	
	public boolean isAttackedByUs(int idx) {
		if(idx < 0 || idx >= getEnemyCount()) {
			throw new IllegalArgumentException();
		}
		
		return enemysWeAttack[idx];
	}

	public boolean isAttackingUs(int idx) {
		if(idx < 0 || idx >= getEnemyCount()) {
			throw new IllegalArgumentException();
		}
		
		return enemysWhoAttackUs[idx];
	}

	public boolean hasWon() {
		for(boolean alive : enemyAlive) {
			if(alive)
				return false;
		}
		return true;
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


	public int getEnemyCount() {
		return enemyNames.length;
	}
	
	public String[] getEnemyNames() {
		return enemyNames;
	}
	
	public int getEnemyIndexByPlayerId(int id) {
		for(int i = 0; i < enemyIds.length; ++i) {
			if(enemyIds[i] == id) {
				return i;
			}
		}
		
		return -1;
	}
	
	public int getPlayerIdByEnemyIndex(int idx) {
		if(idx < 0 || idx >= getEnemyCount()) {
			throw new IllegalArgumentException();
		}
		
		return enemyIds[idx];
	}

	////////////////////////////////////////////// modifiers //////////////////////////////////////////////
	public synchronized void setMoney(int money) {
		if(playerMoney != money) {
			int dm = money - playerMoney;
			playerMoney = money;
			fireMoneyUpdate(dm);
		}
	}
	
	public synchronized int addMoney(int dm) {
		
		setMoney(getPlayerMoney() + dm);
		return playerMoney;
	}

	public synchronized void setStockValues(int[] stockValues) {
		int[] oldValues = this.stockValues;
		this.stockValues = stockValues;
		for(int i = 0; i < getStockNum(); ++i) {
			if(oldValues[i] != stockValues[i])
				fireStockValueUpdate(i, stockValues[i] - oldValues[i]);
		}
	}
	
	public synchronized int buyStocks(int idx) {
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

	
	public synchronized int sellStocks(int idx) {
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

	public synchronized boolean upgradePlayerLevel() {
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
	
	public synchronized int buyUnits(int idx, int amount) {
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
	
	public synchronized boolean setAttacking(int idx, boolean attack) {
		if(idx < 0 || idx >= getEnemyCount()) {
			throw new IllegalArgumentException();
		}

		if(isAttackedByUs(idx) != attack && isEnemyAlive(idx)) {
			enemysWeAttack[idx] = attack;
			fireEnemyStatusUpdate(idx, true, false);
		}
		
		return isAttackedByUs(idx);
	}

	public synchronized boolean setAttackedBy(int idx, boolean attack) {
		if(idx < 0 || idx >= getEnemyCount()) {
			throw new IllegalArgumentException();
		}

		if(isAttackingUs(idx) != attack && isEnemyAlive(idx)) {
			enemysWhoAttackUs[idx] = attack;
			fireEnemyStatusUpdate(idx, true, false);
		}
		
		return isAttackedByUs(idx);
	}
	
	public synchronized void setEnemyDeath(int id, String reason) {
		
		int idx = getEnemyIndexByPlayerId(id);
		
		if(idx != -1) {
			enemyAlive[idx] = false;
			
			fireEnemyStatusUpdate(idx, false, false);
		}
	}
	
	public synchronized void applyWar(int enemyId, boolean wasAttacking, boolean won, int[] lostUnits, int loot, int lifeLost) {
		
		if(lostUnits == null) {
			throw new NullPointerException();
		}
		
		if(enemyId < 0 || enemyId > enemyNames.length || lostUnits.length != units.length || loot < 0 || lifeLost < 0) {
			throw new IllegalArgumentException();
		}
		
		for(int i = 0; i < units.length; ++i) {
			units[i] -= Math.min(units[i], lostUnits[i]);
		}
		
		if(!won) {
			playerLife -= Math.min(playerLife, lifeLost);
		}
		
		addMoney(won? loot : -loot);
		for(int i = 0; i < lostUnits.length; ++i) {
			int du = -lostUnits[i];
			if(du < 0)
				fireUnitsUpdate(i, du);
		}
		
		fireWar(enemyId, wasAttacking, -lifeLost, won);
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

	private void fireEnemyStatusUpdate(int idx, boolean attackChange, boolean defendChange) {
		for(IClientModelListener listener : listeners) {
			listener.onEnemyStatusChange(idx, enemyIds[idx], attackChange, isAttackedByUs(idx), defendChange, isAttackingUs(idx), isEnemyAlive(idx));
		}
	}

	private void fireWar(int idx, boolean wasAttacking, int dlife, boolean won) {
		for(IClientModelListener listener : listeners) {
			listener.onWar(idx, wasAttacking, dlife, won);
		}
	}

}
