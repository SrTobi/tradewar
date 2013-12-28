package tradewar.app.mods.classic.client;

import javax.swing.table.AbstractTableModel;

import tradewar.app.mods.classic.client.ClientModel.IClientModelListener;

public class EnemySelectionModel extends AbstractTableModel {

	private static String[] COLUMN_NAMES = {"Enemy", "Attack", "Defend"};
	private static Class<?>[] COLUMN_CLASSES = {String.class, Boolean.class, Boolean.class};
	private static final long serialVersionUID = 3692954013074397632L;

	private ClientModel model;
	
	public EnemySelectionModel(ClientModel model) {
		this.model = model;
		model.addListener(createModelListener());
	}
	
	@Override
    public String getColumnName(int col) {
        return COLUMN_NAMES[col];
    }
	
	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public int getRowCount() {
		return model.getEnemyCount();
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return COLUMN_CLASSES[columnIndex];
	}

	@Override
    public boolean isCellEditable(int row, int col) {
		return col == 1 && model.isEnemyAlive(row);
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		if(row >= 0 && row < model.getEnemyCount()) {
			switch(col) {
			case 0:
				return model.getEnemyNames()[row];
			case 1:
				return model.isAttackedByUs(row);
			case 2:
				return model.isAttackingUs(row);
			default:
				break;
			}
		}
		return null;
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		if(value instanceof Boolean) {
			boolean attack = (Boolean)value;
			model.setAttacking(row, attack);
		}
	}
	
	private IClientModelListener createModelListener() {
		return new IClientModelListener() {
			
			@Override
			public void onUnitsChange(int idx, int du, int units) {}
			
			@Override
			public void onStockValueChange(int idx, int dv, int value) {}
			
			@Override
			public void onStockAmountChange(int idx, int da, int amount) {}
			
			@Override
			public void onPlayerLevelChange(int dlvl, int lvl) {}
			
			@Override
			public void onMoneyChange(int dm, int money) {}
			
			@Override
			public void onEnemyStatusChange(int idx, int id, boolean attackChange, boolean attack, boolean defendChange, boolean defend, boolean alive) {
				fireTableRowsUpdated(idx, idx);
			}

			@Override
			public void onWar(int idx, boolean wasAttacking, int dlife, boolean won) {}
		};
	}
}
