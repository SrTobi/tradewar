package tradewar.app.mods.classic.client;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JScrollPane;
import javax.swing.JLabel;

import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.BoxLayout;

import tradewar.app.mods.classic.client.ClientModel.IClientModelListener;

public class EconomyView extends JPanel {

	private static final long serialVersionUID = 7529951709724948435L;
	
	private ClientModel model;
	
	private JLabel lblMoneyCount;
	private JButton btnLevelUp;
	private JPanel pnlStockPanel;
	private StockPanel[] stockPanels;

	/**
	 * Create the panel.
	 */
	public EconomyView(ClientModel model) {

		this.model = model;
		
		this.stockPanels = new StockPanel[model.getStockNum()];
		
		setup();
		refreshMoneyLabel();
		refreshCurrentLevelButton();
		setupStocks();
		
		model.addListener(createModelListener());

		validate();
	}
	
	private void setupStocks() {
		String[] stockNames = model.getStockNames();
		int[] stockValues = model.getStockValues();
		
		for(int i = 0; i < stockNames.length; ++i) {
			StockPanel p = stockPanels[i] = new StockPanel(createControl(i), stockNames[i], stockValues[i]);
			pnlStockPanel.add(p);
		}
	}
	
	private SellBuyControl createControl(final int stockIndex) {
		return new SellBuyControl() {
			@Override
			public void sell() {
				model.sellStocks(stockIndex);
			}
			
			@Override
			public void buy() {
				model.buyStocks(stockIndex);
			}
		};
	}
	

	private void setup() {

		setLayout(new MigLayout("", "[][grow][]", "[grow][]"));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(null);
		add(scrollPane, "cell 0 0 3 1,grow");
		
		pnlStockPanel = new JPanel();
		pnlStockPanel.setBorder(null);
		scrollPane.setViewportView(pnlStockPanel);
		pnlStockPanel.setLayout(new BoxLayout(pnlStockPanel, BoxLayout.Y_AXIS));
		
		JLabel lblMoney = new JLabel("Money: ");
		lblMoney.setFont(new Font("Tahoma", Font.PLAIN, 25));
		add(lblMoney, "cell 0 1");
		
		lblMoneyCount = new JLabel();
		lblMoneyCount.setFont(new Font("Tahoma", Font.PLAIN, 25));
		add(lblMoneyCount, "flowx,cell 1 1");
		
		btnLevelUp = new JButton();
		btnLevelUp.setAction(levelUpAction);
		btnLevelUp.setFont(new Font("Tahoma", Font.PLAIN, 18));
		add(btnLevelUp, "cell 2 1,grow");
	}
	
	private IClientModelListener createModelListener() {
		return new IClientModelListener() {

			@Override
			public void onMoneyChange(int dm, int money) {
				refreshMoneyLabel();
			}
			
			@Override
			public void onStockValueChange(int idx, int dv, int value) {
				stockPanels[idx].setStockValue(value);
			}
			
			@Override
			public void onStockAmountChange(int idx, int da, int amount) {
				stockPanels[idx].setStockAmount(amount);
			}
			
			@Override
			public void onPlayerLevelChange(int dlvl, int lvl) {
				refreshCurrentLevelButton();
			}

			@Override
			public void onUnitsChange(int idx, int du, int units) {}
		};
	}
	
	private void refreshMoneyLabel() {
		lblMoneyCount.setText(model.getPlayerMoney() + "$");

		btnLevelUp.setEnabled(model.canUpgradePlayerLevel() && model.getPlayerMoney() >= model.getLevelUpCosts());
	}
	
	private void refreshCurrentLevelButton() {

		StringBuilder builder = new StringBuilder();
		
		// print lvl
		builder.append("Lvl ");
		builder.append(model.getPlayerLevel());
		builder.append(' ');
		
		// print identifier
		builder.append(model.getLevelIdentifier());
		
		// print property
		builder.append(" (");
		builder.append(model.getStocksPerTrade());
		builder.append(" SpT)");
		
		// print upgrade costs
		if(model.canUpgradePlayerLevel()) {
			builder.append(" [+");
			builder.append(model.getLevelUpBonus());
			builder.append(' ');
			builder.append(model.getLevelUpCosts());
			builder.append("$]");
		}
		
		btnLevelUp.setText(builder.toString());
		
	}
	
	private Action levelUpAction = new AbstractAction() {

		private static final long serialVersionUID = 3053116624587042987L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			model.upgradePlayerLevel();
		}
	};

}
