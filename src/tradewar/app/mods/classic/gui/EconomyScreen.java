package tradewar.app.mods.classic.gui;

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

public class EconomyScreen extends JPanel {

	private static final long serialVersionUID = 7529951709724948435L;
	
	public interface SellBuyControl {
		public void buy();
		public void sell();
	}
	
	private final String[] levelNames = {"Imp", "Trader", "Broker", "Mogul", "Exchange god"};
	private final int[] levelUpCosts = {50000, 100000, 800000, 5000000 };
	private final int[] levelAmount = {1, 2, 4, 6, 10 };
	
	private int playerLevel;
	private int money;
	private String[] stockNames;
	private int[] stockValues;
	
	private JLabel lblMoneyCount;
	private JButton btnLevelUp;
	private JPanel pnlStockPanel;
	private StockPanel[] stockPanels;

	/**
	 * Create the panel.
	 */
	public EconomyScreen(int money, String[] stockNames, int[] stockValues) {
		if(stockNames.length != stockValues.length) {
			throw new IllegalArgumentException();
		}
		
		this.playerLevel = 0;
		this.money = money;
		this.stockNames = stockNames;
		this.stockValues = stockValues;
		
		this.stockPanels = new StockPanel[stockNames.length];
		
		setup();
		refreshMoneyLabel();
		refreshCurrentLevelButton();
		setupStocks();

		validate();
	}

	public void setNewStockValues(int[] newStockValues) {
		if(newStockValues.length != stockValues.length) {
			throw new IllegalArgumentException();
		}
		stockValues = newStockValues;
		
		for(int i = 0; i < stockValues.length; ++i) {
			stockPanels[i].setStockValue(stockValues[i]);
		}
	}
	
	private void setupStocks() {
		for(int i = 0; i < stockNames.length; ++i) {
			StockPanel p = stockPanels[i] = new StockPanel(createControl(i), stockNames[i], stockValues[i]);
			pnlStockPanel.add(p);
		}
	}
	
	private SellBuyControl createControl(final int stockIndex) {
		return new SellBuyControl() {
			
			int amount = 0;
			
			@Override
			public void sell() {
				int value = stockValues[stockIndex];
				int sold = getStocksPerTrade();
				if(sold > amount) {
					sold = amount;
				}
				
				if(sold > 0) {
					amount -= sold;
					money += value * sold;
					
					refreshMoneyLabel();
					refreshStockAmount();
				}
			}
			
			@Override
			public void buy() {
				int value = stockValues[stockIndex];
				
				if(money >= 0) {
					int sold = Math.min(getStocksPerTrade(), Math.max(1, money / value));
					
					amount += sold;
					money -= value * sold;
					
					refreshMoneyLabel();
					refreshStockAmount();
				}				
			}

			private void refreshStockAmount() {
				stockPanels[stockIndex].setStockAmount(amount);
			}
			
		};
	}
	
	private int getStocksPerTrade() {
		return levelAmount[playerLevel];
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
	
	private void refreshMoneyLabel() {
		lblMoneyCount.setText(money + "$");

		int maxlvl = levelNames.length - 1;

		btnLevelUp.setEnabled(playerLevel < maxlvl && money >= levelUpCosts[playerLevel]);
	}
	
	private void refreshCurrentLevelButton() {
		btnLevelUp.setText(createLevelString(playerLevel));
		
	}
	
	private String createLevelString(int lvl) {
		int maxlvl = levelNames.length - 1;
		if(lvl < 0 || lvl > maxlvl) {
			throw new IllegalStateException();
		}
		
		StringBuilder builder = new StringBuilder();
		
		// print lvl
		builder.append("Lvl ");
		builder.append(lvl + 1);
		builder.append(' ');
		
		// print identifier
		builder.append(levelNames[lvl]);
		
		// print property
		builder.append(" (");
		builder.append(levelAmount[lvl]);
		builder.append(" SpT)");
		
		// print upgrade costs
		if(lvl < maxlvl) {
			builder.append(" [+");
			builder.append(levelAmount[lvl + 1] - levelAmount[lvl]);
			builder.append(' ');
			builder.append(String.valueOf(levelUpCosts[lvl]));
			builder.append("$]");
		}
		
		return builder.toString();
	}
	
	private Action levelUpAction = new AbstractAction() {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			int maxlvl = levelNames.length - 1;
			
			if(playerLevel < maxlvl) {
				
				int costs = levelUpCosts[playerLevel];
				if(money >= costs) {
					money -= costs;
					++playerLevel;
					refreshMoneyLabel();
					refreshCurrentLevelButton();
				}
			}
		}
	};

}
