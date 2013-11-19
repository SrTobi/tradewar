package tradewar.app.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import tradewar.app.network.QueryResponse;

public class GameOverviewModel extends AbstractTableModel {

	static final String[] COLUMN_NAMES = new String[] {"Server", "Mod", "Passwort", "Spieler", "Ip"};

	
	List<QueryResponse> entries;
	
	
	public GameOverviewModel() {
		clear();
	}
	
	public void addResponse(QueryResponse response) {
		
		if(!entries.contains(response)) {
			entries.add(response);
			fireTableRowsInserted(entries.size() - 1, entries.size());
		}
		
	}
	
	public QueryResponse getRowData(int row) {
		return entries.get(row);
	}

	public void clear() {
		entries = new ArrayList<>();
		fireTableDataChanged();
	}	
	
	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}
	
	@Override
	public String getColumnName(int col) {
		return COLUMN_NAMES[col];
	}
	
	@Override
	public Class<?> getColumnClass(int col) {
		return String.class;
	}

	@Override
	public int getRowCount() {
		return entries.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		return extractFromResString(entries.get(row), col);
	}

	private String extractFromResString(QueryResponse r, int idx) {
		
		switch(idx) {
		case 0:
			return r.getServerName();
		case 1:
			return r.getModification();
		case 2:
			return r.hasPassword() ? "yes" : "no";
		case 3:
			return "" + r.getMaxPlayer();
		case 4:
			return r.getServerAddress();
		}
		
		return null;
	}
}
