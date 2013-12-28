package tradewar.app.mods.classic.client;

import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import tradewar.app.mods.classic.client.ClientModel.IClientModelListener;
import net.miginfocom.swing.MigLayout;

import javax.swing.JSlider;

import java.awt.Dimension;

import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;

public class MilitaryView extends JPanel {
	
	private static final long serialVersionUID = -2195885201414183653L;

	private ClientModel model;
	
	private JLabel lblLive;
	private JTable tblEnemyList;
	private JPanel unitShopPanel;
	private JTextArea txtOutput;
	
	private UnitPanel[] unitPanels;
	private JSlider unitShopSlider;
	private JScrollPane scrollPane;

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
		
		setLayout(new MigLayout("", "[grow,fill]", "[][::40%][::40%,grow][][grow,fill]"));
		
		lblLive = new JLabel("Life: " + model.getPlayerLife());
		lblLive.setFont(new Font("Tahoma", Font.PLAIN, 18));
		add(lblLive, "cell 0 0");
		
		JScrollPane scrollPane_enemyList = new JScrollPane();
		add(scrollPane_enemyList, "cell 0 1,growx,aligny top");
		
		tblEnemyList = new JTable();
		tblEnemyList.setPreferredScrollableViewportSize(new Dimension(450, 50));
		tblEnemyList.setModel(new EnemySelectionModel(model));
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
		add(unitShopPanel, "cell 0 2,grow");
		
		unitShopSlider = new JSlider();
		unitShopSlider.setPaintTicks(true);
		unitShopSlider.setSnapToTicks(true);
		unitShopSlider.setPaintLabels(true);
		unitShopSlider.setMajorTickSpacing(1);
		unitShopSlider.setValue(1);
		unitShopSlider.setMinorTickSpacing(1);
		unitShopSlider.setMinimum(1);
		unitShopSlider.setMaximum(20);
		add(unitShopSlider, "cell 0 3,growx");
		
		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		txtOutput = new JTextArea();
		scrollPane.setViewportView(txtOutput);

		DefaultCaret caret = (DefaultCaret)txtOutput.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		add(scrollPane, "cell 0 4");
		
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

			@Override
			public void onEnemyStatusChange(int idx, int id, boolean attackChange, boolean attack, boolean defendChange, boolean defend, boolean alive) {}

			@Override
			public void onWar(int idx, boolean wasAttacking, int dlife, boolean won) {
				lblLive.setText("Life: " + model.getPlayerLife	());
				
				String enemyName = model.getEnemyNames()[idx];
				if(wasAttacking) {
					if(won) {
						txtOutput.append("Won while attacking " + enemyName + " (" + dlife + " Life)!\n");
					}else{
						txtOutput.append("Lost while attacking " + enemyName + "!!!!!!!!\n");
					}
				}else{
					if(won) {
						txtOutput.append("Successfully defended against " + enemyName + "!\n");
					}else{
						txtOutput.append("Faild to defend against " + enemyName + "!!!!!!!!\n");
					}
				}
			}
		};
	}
}
