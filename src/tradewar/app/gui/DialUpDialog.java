package tradewar.app.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;

import net.miginfocom.swing.MigLayout;

import javax.swing.JProgressBar;
import javax.swing.JLabel;

import tradewar.api.ISocket;
import tradewar.app.Application;
import tradewar.app.network.ConnectionBuilder;
import tradewar.utils.log.Log;

public class DialUpDialog extends JDialog {

	private Log log = new Log(Application.LOGSTREAM, "dailup-dlg");
	private ConnectionBuilder builder;
	
	private int port;
	private String address;
	
	private Action cancelAction = new CancelAction();
	
	public DialUpDialog(String addr, final int port) {
		
		this.address = addr;
		this.port = port;
		
		builder = new ConnectionBuilder(log.getStream(), addr, port) {
			
			@Override
			public void onFailed(IOException e) {
				DialUpDialog.this.dispose();
				
				ExceptionDialog.normalFail("Connection failed!", "Failed to connect to " + address + ":" + port, e, log);
			}
			
			@Override
			public void onConnected(ISocket socket) {
				log.info("Connected!");
				DialUpDialog.this.dispose();
			}
			
			@Override
			public void onCanceled() {
				log.err("Connection canceled!");
				DialUpDialog.this.dispose();
			}
		};
		
		setup();
		
		addWindowListener(new WindowAdapter() {
			@Override
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
		setMinimumSize(new Dimension(400, 120));
		getContentPane().setLayout(new MigLayout("", "[66.00,grow][]", "[grow][25px:n][grow]"));
		
		JLabel lblNewLabel = new JLabel("Connecting to " + address + ", Port " + port);
		getContentPane().add(lblNewLabel, "cell 0 0 2 1,growx,aligny center");
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		getContentPane().add(progressBar, "cell 0 1 2 1,grow");
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setAction(cancelAction);
		getContentPane().add(btnCancel, "cell 1 2,alignx right,aligny bottom");

		setResizable(false);
		setLocationRelativeTo(null);
		
		pack();
	}
	
	class CancelAction extends AbstractAction {


		public CancelAction() {
			putValue(NAME, "Cancel");
			putValue(SHORT_DESCRIPTION, "Cancels connection attempt");
		}
		
		
		@Override
		public void actionPerformed(ActionEvent evt) {
			builder.cancel();
			DialUpDialog.this.dispose();
		}
		
	}

}
