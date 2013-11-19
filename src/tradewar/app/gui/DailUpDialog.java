package tradewar.app.gui;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.Socket;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import net.miginfocom.swing.MigLayout;

import javax.swing.JProgressBar;
import javax.swing.JLabel;

import tradewar.app.Application;
import tradewar.app.network.ConnectionBuilder;
import tradewar.utils.log.Log;

public class DailUpDialog extends JDialog {

	private Log log = new Log(Application.LOGSTREAM, "dailup-dlg");
	private ConnectionBuilder builder;
	
	private int port;
	private String address;
	
	private Action cancelAction = new CancelAction();
	
	public DailUpDialog(String addr, final int port) {
		
		this.address = addr;
		this.port = port;
		
		builder = new ConnectionBuilder(addr, port) {
			
			@Override
			public void onFailed(IOException e) {
				log.err("Connection failed!");
				log.excp(e);
				DailUpDialog.this.setVisible(false);
				JOptionPane.showMessageDialog(null, "Failed to connect to " + address + ":" + port, "Connection error!", JOptionPane.ERROR_MESSAGE);
			}
			
			@Override
			public void onConnected(Socket socket) {
				log.info("Connected!");
				DailUpDialog.this.dispose();
			}
			
			@Override
			public void onCanceled() {
				log.err("Connection canceled!");
				DailUpDialog.this.dispose();
			}
		};
		
		setup();
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
		        builder.cancel();
			}
		});
		
		// Start connecting
		builder.connect();
	}
	
	public void setup() {
		setTitle("Connecting...");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);

		setBounds(100, 100, 400, 120);
		getContentPane().setLayout(new MigLayout("", "[66.00,grow][]", "[grow][25px:n][grow]"));
		
		JLabel lblNewLabel = new JLabel("Connecting to " + address + ", Port " + port);
		getContentPane().add(lblNewLabel, "cell 0 0 2 1,growx,aligny center");
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		getContentPane().add(progressBar, "cell 0 1 2 1,grow");
		
		JButton btnCancel = new JButton("Cancel");
		getContentPane().add(btnCancel, "cell 1 2,alignx right,aligny bottom");

		setResizable(false);
		setLocationRelativeTo(null);
	}
	
	class CancelAction extends AbstractAction {


		public CancelAction() {
			putValue(NAME, "Cancel");
			putValue(SHORT_DESCRIPTION, "Cancels connection attempt");
		}
		
		
		@Override
		public void actionPerformed(ActionEvent evt) {
			builder.cancel();
		}
		
	}

}
