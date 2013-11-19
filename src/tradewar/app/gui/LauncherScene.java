package tradewar.app.gui;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import net.miginfocom.swing.MigLayout;
import tradewar.api.IApp;
import tradewar.api.IMod;
import tradewar.api.IModInfo;
import tradewar.api.IScene;
import tradewar.api.IServer;
import tradewar.api.IServerStartParams;
import tradewar.app.Application;
import tradewar.app.ConfigManager;
import tradewar.app.ModManager;
import tradewar.app.network.ListenServer;
import tradewar.app.network.QueryEmitter;
import tradewar.app.network.QueryResponse;
import tradewar.app.network.QueryResponseListener;
import tradewar.app.network.QueryServer;
import tradewar.utils.log.Log;

public class LauncherScene extends JPanel implements IScene {

	private static final long serialVersionUID = 4078132421255645474L;
	
	private Log log = new Log(Application.LOGSTREAM, "launcher-scene");
	private IApp app;
	private ModManager modManager;
	private ConfigManager configManager;
	
	private QueryEmitter queryEmitter;
	private GameOverviewModel gameOverviewModel;
	
	private int standardQueryServerPort;
	private int standardGameServerPort;
	
	private JTable gameOverview;
	private JFormattedTextField nicknameInput;
	private JLabel lblInfoLabel;
	private JPanel panel;
	private JTextField specificQueryServerPortInput;
	private JScrollPane gameOverviewScrollPane;
	private JCheckBox specificQueryServerPortCheckBox;

	private final Action quitAction = new QuitAction();
	private final Action enableQueryServerPortInputAction = new EnableQueryServerPortInputAction();
	private final Action refreshServerOverview = new RefreshServerOverviewAction();
	private final Action connectAction = new ConnectAction();
	private final Action createServerAction = new CreateServerAction();
	private final Action directConnectAction = new DirectConnectAction();
	
	/**
	 * Create the panel.
	 */
	public LauncherScene(IApp app, ConfigManager configManager, ModManager modManager, int standardGameServerPort, int standardQueryServerPort) {
		
		this.app = app;
		this.configManager = configManager;
		this.modManager = modManager;
		this.standardGameServerPort = standardGameServerPort;
		this.standardQueryServerPort = standardQueryServerPort;
		
		setup();
	}
	
	private void setup() {

		log.debug("setup launcher scene...");
		setBorder(new EmptyBorder(4, 10, 10, 10));
		setLayout(new MigLayout("", "[][][][grow,fill][]", "[][][][grow][][][][]"));
		
		JLabel lblTradewar = new JLabel("TradeWar");
		lblTradewar.setFont(new Font("Tahoma", Font.BOLD, 40));
		lblTradewar.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblTradewar, "flowx,cell 0 0 5 1,growx");
		
		JLabel lblNickname = new JLabel("Nickname:");
		add(lblNickname, "cell 0 1,alignx trailing");
		
		nicknameInput = new JFormattedTextField();
		add(nicknameInput, "cell 1 1 3 1,growx");
		
		JPanel specificPortGroup = new JPanel();
		specificPortGroup.setBorder(new TitledBorder(null, "Port", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(specificPortGroup, "cell 4 2,grow");
		specificPortGroup.setLayout(new BoxLayout(specificPortGroup, BoxLayout.X_AXIS));
		
		specificQueryServerPortCheckBox = new JCheckBox("");
		specificQueryServerPortCheckBox.setAction(enableQueryServerPortInputAction);
		specificPortGroup.add(specificQueryServerPortCheckBox);
		
		specificQueryServerPortInput = new JTextField();
		specificQueryServerPortInput.setEnabled(false);
		resetStandardQueryPortInputField();
		specificPortGroup.add(specificQueryServerPortInput);
		
		gameOverviewScrollPane = new JScrollPane();
		add(gameOverviewScrollPane, "cell 0 2 4 5,grow");
		
		gameOverview = new JTable();
		gameOverviewScrollPane.setViewportView(gameOverview);
		gameOverview.setFillsViewportHeight(true);
		gameOverview.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		gameOverview.setModel(gameOverviewModel = new GameOverviewModel());
		gameOverview.getColumnModel().getColumn(0).setPreferredWidth(100);
		gameOverview.getColumnModel().getColumn(0).setMinWidth(50);
		gameOverview.getColumnModel().getColumn(1).setPreferredWidth(100);
		gameOverview.getColumnModel().getColumn(1).setMinWidth(50);
		gameOverview.getColumnModel().getColumn(2).setPreferredWidth(60);
		gameOverview.getColumnModel().getColumn(2).setMinWidth(60);
		gameOverview.getColumnModel().getColumn(2).setMaxWidth(80);
		gameOverview.getColumnModel().getColumn(3).setPreferredWidth(60);
		gameOverview.getColumnModel().getColumn(3).setMinWidth(45);
		gameOverview.getColumnModel().getColumn(3).setMaxWidth(60);
		gameOverview.getColumnModel().getColumn(4).setPreferredWidth(100);
		gameOverview.getColumnModel().getColumn(4).setMinWidth(90);
		gameOverview.getColumnModel().getColumn(4).setMaxWidth(135);
		
		JButton btnCreateServerButton = new JButton("Server");
		btnCreateServerButton.setAction(createServerAction);
		add(btnCreateServerButton, "cell 4 4,growx");
		
		JButton btnRefreshButton = new JButton("Refresh");
		btnRefreshButton.setAction(refreshServerOverview);
		add(btnRefreshButton, "cell 4 5,growx");
		
		JButton btnDirectConnectButton = new JButton("Direct Connect");
		btnDirectConnectButton.setAction(directConnectAction);
		add(btnDirectConnectButton, "cell 4 6,growx");
		
		JButton btnQuitButton = new JButton("Quit");
		btnQuitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnQuitButton.setAction(quitAction);
		add(btnQuitButton, "cell 0 7");
		
		JButton btnOpenHelpButton = new JButton("Help");
		add(btnOpenHelpButton, "cell 1 7");
		
		JButton btnConfigButton = new JButton("Config");
		add(btnConfigButton, "cell 2 7");
		
		panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		add(panel, "cell 3 7,grow");
		
		lblInfoLabel = new JLabel("Tobias");
		panel.add(lblInfoLabel);
		lblInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		JButton btnConnectButton = new JButton("Connect");
		btnConnectButton.setAction(connectAction);
		add(btnConnectButton, "cell 4 7,growx");
		
	}
	

	@Override
	public Component getView() {
		return this;
	}

	@Override
	public String getSceneName() {
		return "launcher-scene";
	}

	@Override
	public String getSceneTitle() {
		return "Launcher";
	}

	@Override
	public void onRegister() {}
	@Override
	public void onUnregister() {}
	@Override
	public void onEnter() {}
	@Override
	public void onLeave() {}
	
	private void resetStandardQueryPortInputField() {

		specificQueryServerPortInput.setText("" + standardQueryServerPort);
	}

	private class QuitAction extends AbstractAction {
		public QuitAction() {
			putValue(NAME, "Quit");
			putValue(SHORT_DESCRIPTION, "Quit the application");
		}
		public void actionPerformed(ActionEvent e) {
			
			log.debug("Quit pressed!");			
			app.getMainSceneFrame().removeScene(LauncherScene.this);
		}
	}
	
	
	private class EnableQueryServerPortInputAction extends AbstractAction {
		public EnableQueryServerPortInputAction() {
			putValue(NAME, "");
			putValue(SHORT_DESCRIPTION, "Enables the port for the server hook!");
		}
		
		public void actionPerformed(ActionEvent e) {
			boolean enableSpecPort = specificQueryServerPortCheckBox.isSelected();
			
			if(!enableSpecPort) {
				resetStandardQueryPortInputField();
			}
			
			specificQueryServerPortInput.setEnabled(enableSpecPort);
		}
	}

	private class RefreshServerOverviewAction extends AbstractAction implements QueryResponseListener{
		public RefreshServerOverviewAction() {
			putValue(NAME, "Refresh");
			putValue(SHORT_DESCRIPTION, "Searches for game servers.");
		}
		
		public void actionPerformed(ActionEvent evt) {

			if(queryEmitter != null && queryEmitter.isSearching()) {
				queryEmitter.search(false);
				queryEmitter.removeResponseListener(this);
			}
			
			gameOverviewModel.clear();
			
			try {
				queryEmitter = new QueryEmitter(log.getStream(), standardQueryServerPort);
				queryEmitter.addResponseListener(this);
				queryEmitter.search(true);
			} catch (IOException e) {
				log.crit("Failed to create query emitter!");
				log.excp(e);
			}
		}

		@Override
		public void onResponse(final QueryResponse response) {
			
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					gameOverviewModel.addResponse(response);
				}
			});
		}

		@Override
		public void onSearchStop() {
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					queryEmitter = null;
				}
			});			
		}
	}
	

	private class ConnectAction extends AbstractAction {
		public ConnectAction() {
			putValue(NAME, "Connect");
			putValue(SHORT_DESCRIPTION, "Connect to a server.");
		}
		
		public void actionPerformed(ActionEvent evt) {
			
			int row = gameOverview.getSelectedRow();
			
			if(row < 0) {
				return;
			}
			
		 	QueryResponse r = gameOverviewModel.getRowData(row);
		 	
		 	DailUpDialog dlg = new DailUpDialog(r.getServerAddress(), r.getServerPort());
			dlg.setVisible(true);
		}
	}

	private class DirectConnectAction extends AbstractAction {
		public DirectConnectAction() {
			putValue(NAME, "Direct Connect");
			putValue(SHORT_DESCRIPTION, "Connect to a server.");
		}
		
		public void actionPerformed(ActionEvent evt) {

			String ip = (String)JOptionPane.showInputDialog(null,
										                    "Please enter the address of the target machine:",
										                    "Direct Connect",
										                    JOptionPane.PLAIN_MESSAGE,
										                    null,
										                    null,
										                    "");
			
			if(ip != null) {

				int port = standardGameServerPort;				
				String[] parts = ip.split(":");
				
				if(parts.length >= 2) {
					ip = parts[0];
					try {
						port = Integer.parseInt(parts[1]);
					} catch(NumberFormatException e) 
					{
						log.err("Direct connect address does not contains a port number after the ':'!");
					}
					
				}
				
			 	DailUpDialog dlg = new DailUpDialog(ip, port);
				dlg.setVisible(true);
			}
		}
	}
	
	private class CreateServerAction extends AbstractAction {
		public CreateServerAction() {
			putValue(NAME, "Server");
			putValue(SHORT_DESCRIPTION, "Open server creation dialog.");
		}
		
		public void actionPerformed(ActionEvent evt) {
			
			IModInfo[] mods = modManager.getModInfos();
			
			if(mods.length == 0) {
				
				JOptionPane.showMessageDialog(null, "No mods are installed! Please get mods and put them into the mod folder!", "No mods to start!", JOptionPane.ERROR_MESSAGE);
				
				return;
			}			
			
			ServerCreationDialog scdlg = new ServerCreationDialog(mods, standardGameServerPort, standardQueryServerPort);
			
			if(scdlg.showDialog()) {
				log.debug("Create Server dialog closed successfully!");
				
				IModInfo modInfo = scdlg.getSelectedMod();
				IServerStartParams ssparams = scdlg.getStartParams();
				

				ListenServer lsrv;
				try {
					lsrv = new ListenServer(log.getStream(), ssparams);
				} catch (IOException e) {
					log.err("Failed to create listen-server!");
					log.excp(e);
					return;
				}
				
				QueryServer qsrv = new QueryServer(ssparams);
				
				IMod mod = modManager.startMod(modInfo);
				IServer server = mod.createDedicatedServer(ssparams, lsrv, qsrv);
				
				
				if(server == null) {
					log.crit("Failed to start modification!");
					return;
				}
				
				lsrv.setServerListener(server);
				qsrv.setServer(server);
				
				qsrv.setActive(true);
			}
		}
	}
}
