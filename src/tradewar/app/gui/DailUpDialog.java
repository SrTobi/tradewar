package tradewar.app.gui;

import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JDialog;

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
	
	public DailUpDialog(String addr, int port) {
		
		this.address = addr;
		this.port = port;
		
		builder = new ConnectionBuilder(addr, port) {
			
			@Override
			public void onFailed(IOException e) {
				log.err("Connection failed!");
				log.excp(e);
			}
			
			@Override
			public void onConnected(Socket socket) {
				log.info("Connected!");
			}
			
			@Override
			public void onCanceled() {
				log.err("Connection canceled!");
			}
		};
		
		setup();
		
		builder.connect();
	}
	
	public void setup() {
		setTitle("Connecting...");
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

}
