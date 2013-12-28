package tradewar.app.mods.classic.client;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.JButton;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UnitPanel extends JPanel {

	private static final long serialVersionUID = 4730500686760279823L;
	
	private JLabel lblPriceLabel;
	private JButton btnBuyButton;
	private JLabel lblUnitCount;
	
	private SellBuyControl control;
	private String unitName;
	private int unitPrice;

	public UnitPanel(String unitName, int unitPrice, SellBuyControl control) {
		if(unitName == null || control == null) {
			throw new NullPointerException();
		}
		
		this.unitName = unitName;
		this.unitPrice = unitPrice;
		this.control = control;
		
		
		setup();
	}

	private void setup() {
		setLayout(new MigLayout("", "[grow][60px:60px:60px][70px:70px:70px,center][]", "[grow,center]"));
		
		JLabel lblUnitName = new JLabel(unitName);
		lblUnitName.setFont(new Font("Tahoma", Font.PLAIN, 14));
		add(lblUnitName, "cell 0 0");
		
		lblPriceLabel = new JLabel(unitPrice + "$");
		lblPriceLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		add(lblPriceLabel, "cell 1 0,alignx right");
		
		lblUnitCount = new JLabel("0");
		lblUnitCount.setFont(new Font("Tahoma", Font.PLAIN, 14));
		add(lblUnitCount, "cell 2 0,alignx center");
		
		btnBuyButton = new JButton("Buy");
		btnBuyButton.setMinimumSize(new Dimension(80, 23));
		btnBuyButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		add(btnBuyButton, "cell 3 0,alignx right,aligny center");
		
		btnBuyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				control.buy();
			}
		});
	}

	public void setUnitCount(int amount) {
		lblUnitCount.setText(String.valueOf(amount));
	}
}
