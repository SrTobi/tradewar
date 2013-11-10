package tradewar.app.gui;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;
import tradewar.api.IApp;
import tradewar.api.IScene;
import tradewar.utils.log.ILogStream;
import tradewar.utils.log.Log;
import javax.swing.Action;

public class LauncherScene extends JPanel implements IScene {

	private static final long serialVersionUID = 4078132421255645474L;

	private Log log;
	private IApp app;
	private JTable gameOverview;
	private JLabel lblTradewar;
	private JFormattedTextField nicknameInput;
	private JLabel lblNickname;
	private JLabel lblInfoLabel;
	private JPanel panel;
	private final Action quitAction = new QuitAction();
	
	
	/**
	 * Create the panel.
	 */
	public LauncherScene(ILogStream logStream, IApp app) {
		setBorder(new EmptyBorder(4, 10, 10, 10));

		this.log = new Log(logStream, "launcher-scene");
		this.app = app;
		
		setup();
	}
	
	private void setup() {

		log.debug("setup launcher scene...");
		setLayout(new MigLayout("", "[][][][grow,fill][]", "[][][grow][][][][]"));
		
		lblTradewar = new JLabel("TradeWar");
		lblTradewar.setFont(new Font("Tahoma", Font.BOLD, 40));
		lblTradewar.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblTradewar, "flowx,cell 0 0 5 1,growx");
		
		lblNickname = new JLabel("Nickname:");
		add(lblNickname, "cell 0 1,alignx trailing");
		
		nicknameInput = new JFormattedTextField();
		add(nicknameInput, "cell 1 1 3 1,growx");
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, "cell 0 2 4 4,grow");
		
		gameOverview = new JTable();
		scrollPane.setViewportView(gameOverview);
		gameOverview.setFillsViewportHeight(true);
		gameOverview.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		gameOverview.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Server", "Mod", "Passwort", "Spieler", "Ip"
			}
		) {
			Class<?>[] columnTypes = new Class[] {
				String.class, String.class, Boolean.class, Object.class, String.class
			};
			public Class<?> getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		
		JButton btnQuitButton = new JButton("Quit");
		btnQuitButton.setAction(quitAction);
		add(btnQuitButton, "cell 4 3,growx");
		
		JButton btnRefreshButton = new JButton("Refresh");
		add(btnRefreshButton, "cell 4 4,growx");
		
		JButton btnDirectConnectButton = new JButton("Direct Connect");
		add(btnDirectConnectButton, "cell 4 5,growx");
		
		JButton btnCreateServerButton = new JButton("Server");
		add(btnCreateServerButton, "cell 0 6 2 1");
		
		JButton btnOpenHelp = new JButton("Help");
		add(btnOpenHelp, "cell 2 6");
		
		panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		add(panel, "cell 3 6,grow");
		
		lblInfoLabel = new JLabel("Tobias");
		panel.add(lblInfoLabel);
		lblInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		JButton btnConnectButton = new JButton("Connect");
		add(btnConnectButton, "cell 4 6,growx");
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
}
