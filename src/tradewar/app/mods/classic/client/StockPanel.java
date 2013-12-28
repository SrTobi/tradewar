package tradewar.app.mods.classic.client;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class StockPanel extends JPanel {

	private static final long serialVersionUID = -4201699051555235602L;

	private static final Color UpColor = new Color(0, 100, 0);
	private static final Color DownColor = Color.RED;
	
	private JLabel lblStockValue;
	
	private String stockName;
	private int stockValue;

	private SellBuyControl control;
	private JLabel lblStockAmount;
	
	
	/**
	 * Create the panel.
	 */
	public StockPanel(SellBuyControl control, String name, int value) {
		this.control = control;
		stockName = name;
		stockValue = value;
		
		setup();
	}

	private void setup() {
		setLayout(new MigLayout("", "[grow][120px][100px][100px:n][100px:n]", "[grow]"));
		
		JLabel lblStockName = new JLabel(stockName);
		lblStockName.setFont(new Font("Tahoma", Font.PLAIN, 34));
		add(lblStockName, "cell 0 0");
		
		lblStockValue = new JLabel(String.valueOf(stockValue));
		lblStockValue.setFont(new Font("Tahoma", Font.PLAIN, 20));
		add(lblStockValue, "cell 1 0,alignx left,aligny center");
		
		lblStockAmount = new JLabel("0");
		lblStockAmount.setFont(new Font("Tahoma", Font.PLAIN, 20));
		add(lblStockAmount, "cell 2 0,alignx center,aligny center");
		
		JButton btnBuyButton = new JButton("Buy");
		btnBuyButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		add(btnBuyButton, "cell 3 0,growx,aligny center");
		btnBuyButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				control.buy();
			}
		});
		
		JButton btnSellButton = new JButton("Sell");
		btnSellButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		add(btnSellButton, "cell 4 0,growx,aligny center");
		btnSellButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				control.sell();
			}
		});
	}
	
	public int getValue() {
		return stockValue;
	}
	
	public void setStockValue(int newValue) {
		
		if(stockValue == newValue) {
			return;
		}
		
		lblStockValue.setForeground(newValue > stockValue? UpColor : DownColor);
		
		stockValue = newValue;
		lblStockValue.setText(stockValue + "$");
	}

	public void setStockAmount(int amount) {
		lblStockAmount.setText(String.valueOf(amount));
	}
}
