package tradewar.app.mods.classic.client;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;

import tradewar.app.mods.classic.client.ClientModel.IClientModelListener;
import net.miginfocom.swing.MigLayout;
import javax.swing.JSlider;

public class MilitaryView extends JPanel {
	
	private static final long serialVersionUID = -2195885201414183653L;

	private ClientModel model;
	
	private JTable tblEnemyList;
	private JPanel unitShopPanel;
	private JTextPane txtOutput;
	
	private UnitPanel[] unitPanels;
	private JSlider unitShopSlider;

	/**
	 * Create the panel.
	 */
	public MilitaryView(ClientModel model) {
		this.model = model;
		setup();
		
		validate();
		
		model.addListener(createModelListener());
	}
	
	private void setup() {
		
		setLayout(new MigLayout("", "[grow]", "[::40%][::40%,grow][][grow]"));
		
		JScrollPane scrollPane_enemyList = new JScrollPane();
		add(scrollPane_enemyList, "cell 0 0,growx,aligny top");
		
		tblEnemyList = new JTable();
		tblEnemyList.setModel(new DefaultTableModel(
			new Object[][] {createTableContent()},
			new String[] {"Enemy", "Attack", "Defend"}
		) {
			private static final long serialVersionUID = 1L;
			
			Class<?>[] columnTypes = new Class<?>[] {	String.class, Boolean.class, Boolean.class };
			public Class<?> getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		tblEnemyList.getColumnModel().getColumn(1).setResizable(false);
		tblEnemyList.getColumnModel().getColumn(1).setPreferredWidth(45);
		tblEnemyList.getColumnModel().getColumn(2).setResizable(false);
		tblEnemyList.getColumnModel().getColumn(2).setPreferredWidth(45);
		scrollPane_enemyList.setViewportView(tblEnemyList);
		
		unitShopPanel = new JPanel();
		unitShopPanel.setLayout(new BoxLayout(unitShopPanel, BoxLayout.Y_AXIS));
		{
			String[] unames = model.getUnitNames();
			int[] costs = model.getUnitCosts();
			
			unitPanels = new UnitPanel[unames.length];
			
			for(int i = 0; i < unames.length; ++i) {
				UnitPanel p = unitPanels[i] = new UnitPanel(unames[i], costs[i], createSellBuyControl(i));
				unitShopPanel.add(p);
			}
		}
		add(unitShopPanel, "cell 0 1,grow");
		
		unitShopSlider = new JSlider();
		unitShopSlider.setPaintTicks(true);
		unitShopSlider.setSnapToTicks(true);
		unitShopSlider.setPaintLabels(true);
		unitShopSlider.setMajorTickSpacing(1);
		unitShopSlider.setValue(1);
		unitShopSlider.setMinorTickSpacing(1);
		unitShopSlider.setMinimum(1);
		unitShopSlider.setMaximum(20);
		add(unitShopSlider, "cell 0 2,growx");
		
		txtOutput = new JTextPane();
		add(txtOutput, "cell 0 3,grow");
	}
	
	private Object[][] createTableContent() {
		String[] enemys = model.getEnemyNames();
		
		Object[][] content = new Object[enemys.length][];
		
		for(int i = 0; i < enemys.length; ++i) {
			content[i] = new Object[]{enemys[i], false, false};
		}
		
		return content;
	}
	
	private SellBuyControl createSellBuyControl(final int i) {
		return new SellBuyControl() {
			
			@Override
			public void sell() {
				throw new AssertionError("Should not be used!");
			}
			
			@Override
			public void buy() {
				int bought = model.buyUnits(i, unitShopSlider.getValue());
				unitShopSlider.setValue(bought);
			}
		};
	}

	private IClientModelListener createModelListener() {
		return new IClientModelListener() {

			@Override
			public void onMoneyChange(int dm, int money) {}

			@Override
			public void onStockValueChange(int idx, int dv, int value) {}

			@Override
			public void onStockAmountChange(int idx, int da, int amount) {}

			@Override
			public void onPlayerLevelChange(int dlvl, int lvl) {}

			@Override
			public void onUnitsChange(int idx, int du, int units) {
				unitPanels[idx].setUnitCount(units);
			}
		};
	}
}
