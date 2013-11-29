package tradewar.app.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;
import tradewar.app.Application;
import tradewar.app.network.ClientsideHandshakeProtocol;
import tradewar.app.network.ConnectionBuilder;
import tradewar.app.network.IProtocolListener;
import tradewar.utils.HashedPassword;
import tradewar.utils.log.Log;

public class DialUpDialog extends JDialog {

	private static final long serialVersionUID = 2303049258868397256L;

	private Log log = new Log(Application.LOGSTREAM, "dailup-dlg");
	private ConnectionBuilder builder;
	private ClientsideHandshakeProtocol handshakeProtocol;
	private IProtocolListener handshakeListener;
	
	private String password = "";
	
	private int port;
	private String address;
	
	private Action cancelAction = new CancelAction();
	private JLabel lblStatusLabel;
	
	public DialUpDialog(final String nickname, String addr, final int port) {
		
		this.address = addr;
		this.port = port;
		
		builder = new ConnectionBuilder(addr, port);
		builder.addProtocolListener(new IProtocolListener() {
			
			@Override
			public void onProtocolFail(Exception failure) {
				DialUpDialog.this.dispose();
				
				ExceptionDialog.normalFail("Connection failed!", "Failed to connect to " + address + ":" + port, failure, log);
			}
			
			@Override
			public void onProtocolCompleteness() {
				log.info("Connected!");
				
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						lblStatusLabel.setText("Login...");
					}
				});
				
				handshakeProtocol = new ClientsideHandshakeProtocol(builder.getConnection(), nickname, HashedPassword.fromClean(password));
				handshakeProtocol.addProtocolListener(handshakeListener);
				handshakeProtocol.invokeProtocol();
			}
			
			@Override
			public void onProtocolAbort() {
				log.err("Connection canceled!");
				DialUpDialog.this.dispose();
			}
		});
		
		handshakeListener = new IProtocolListener() {
			
			@Override
			public void onProtocolFail(Exception failure) {
				// failed
			}
			
			@Override
			public void onProtocolCompleteness() {
				log.info("Login!");
				DialUpDialog.this.dispose();
			}
			
			@Override
			public void onProtocolAbort() {
				log.err("Login canceled!");
				DialUpDialog.this.dispose();
			}
		};
		
		setup();
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
		        builder.abort();
			}
		});
		
		// Start connecting
		builder.invokeProtocol();
	}
	
	public void setup() {
		setTitle("Connecting...");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);

		setBounds(100, 100, 400, 120);
		setMinimumSize(new Dimension(400, 120));
		getContentPane().setLayout(new MigLayout("", "[66.00,grow][]", "[grow][25px:n][grow]"));
		
		lblStatusLabel = new JLabel("Connecting to " + address + ", Port " + port);
		getContentPane().add(lblStatusLabel, "cell 0 0 2 1,growx,aligny center");
		
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

		private static final long serialVersionUID = 8837039819400458762L;


		public CancelAction() {
			putValue(NAME, "Cancel");
			putValue(SHORT_DESCRIPTION, "Cancels connection attempt");
		}
		
		
		@Override
		public void actionPerformed(ActionEvent evt) {
			builder.abort();
			DialUpDialog.this.dispose();
		}
		
	}

}
