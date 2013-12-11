package tradewar.app;


import java.awt.EventQueue;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import tradewar.api.IApp;
import tradewar.api.IClient;
import tradewar.api.IConfig;
import tradewar.api.IDirectory;
import tradewar.api.ILogStream;
import tradewar.api.IMod;
import tradewar.api.IModInfo;
import tradewar.api.ISceneFrame;
import tradewar.api.IServer;
import tradewar.api.IServerStartParams;
import tradewar.api.ISocket;
import tradewar.app.gui.ApplicationWindow;
import tradewar.app.gui.ExceptionDialog;
import tradewar.app.gui.LauncherScene;
import tradewar.app.gui.ServerFrame;
import tradewar.app.gui.ServerTerminal;
import tradewar.app.gui.ServerWindow;
import tradewar.app.network.ClientsideHandshakeProtocol;
import tradewar.app.network.ConnectionBuilder;
import tradewar.app.network.ListenServer;
import tradewar.app.network.QueryServer;
import tradewar.utils.HashedPassword;
import tradewar.utils.ProtocolProgressDialog;
import tradewar.utils.Version;
import tradewar.utils.log.Log;

public class Application implements IApp, Runnable {

	public static final Version APP_VERSION = new Version(	"TradeWar",
															1, 1,
															21, 11, 2013);
	public static final ILogStream LOGSTREAM = ILogStream.sys;
	
	private static final int STANDARD_GAMESERVER_PORT = 23451;
	private static final int STANDARD_QUERYSERVER_PORT = 23452;
	
	private Log log = new Log(LOGSTREAM, "app");
	private FileManager fileManager;
	private ModManager modManager;
	private ConfigManager configManager;
	private IDirectory rootDirectory;
	private ApplicationWindow mainWin;
	private ServerWindow serverWin;
	private IClient currentClient;
	
	public Application(String[] args) {

	}

	@Override
	public void run() {

		log.info("Start application...");
		log.info("Version is: " + APP_VERSION.getVersionCode());
		
        try {
        	log.debug("Setup look and feel...");
			UIManager.setLookAndFeel(
			    UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			log.err("Failed to set look and feel!");
			log.excp(e);
		}
        
        UIManager.put("ProgressBar.repaintInterval", new Integer(10));
        UIManager.put("ProgressBar.cycleTime", new Integer(6000));
        
        try {
			fileManager = new FileManager(LOGSTREAM);
			rootDirectory = fileManager.getRoot();
		} catch (IOException e) {
					
			log.crit("Failed to initialize file manager!");
			log.excp(e);
			log.crit("Exiting...");
	
			return;
		}
        
        configManager = new ConfigManager(rootDirectory.getSubDirectory("config", true));
        
        IConfig modManagerConfig = configManager.getConfig("mod-manager.cfg");
        IConfig launcherSceneConfig = configManager.getConfig("launcher-scene.cfg");
        
        IStartableModInfo[] innerMods = new IStartableModInfo[] { tradewar.app.mods.classic.TradeWarClassic.INST };
        modManager = new ModManager(this, configManager, rootDirectory.getSubDirectory("mods", true), modManagerConfig, innerMods);
        
        
        
		
		mainWin = new ApplicationWindow(log.getStream(), "MainWindow");
		mainWin.setScene(new LauncherScene(this, launcherSceneConfig, modManager, STANDARD_GAMESERVER_PORT, STANDARD_QUERYSERVER_PORT));
		
		mainWin.setVisible(true);
	}

	public void startServer(IServerStartParams ssparams) throws AppException {

		IModInfo modInfo = ssparams.getMod();

		log.info("Create Server \"" + ssparams.getServerName() + "\"!");

		ListenServer lsrv;
		try {
			lsrv = new ListenServer(log.getStream(), ssparams);
		} catch (IOException e) {
			throw new AppException("Failed to create listen-server!", e);
		}
		
		QueryServer qsrv = new QueryServer(ssparams);
		
		IMod mod = modManager.startMod(modInfo);

		if(mod == null) {
			throw new AppException("Failed to start modification!");
		}
		
		
		IServer server = mod.createDedicatedServer(ssparams, lsrv, qsrv);

		if(server == null) {
			throw new AppException("Failed to start server!");
		}
		
		
		lsrv.setServerListener(server);
		qsrv.setServer(server);

		lsrv.listen(true);
		qsrv.setActive(true);
		
		
		ServerTerminal terminal = new ServerTerminal(server);
		ServerFrame serverFrame = new ServerFrame(ssparams.getServerName(), server);
		serverFrame.setScene(terminal);
		
		getServerWindow().addServerFrame(serverFrame);
		getServerWindow().setVisible(true);
		
		server.start(terminal.getPrintStream(), serverFrame);
	}

	public void connectToServer(String nickname, String addr, int port) {

		if(currentClient != null) {
			currentClient.stop();
			currentClient = null;
		}
		
	 	/*DialUpDialog dlg = new DialUpDialog(nickname, addr, port);
		dlg.setVisible(true);*/
		
		ConnectionBuilder conbuilder = new ConnectionBuilder(addr, port);
		
		switch(new ProtocolProgressDialog("Connecting to " + addr + " on port " + port, conbuilder).showDlg()) {
			case Aborted:
				return;
			case Failed:
				ExceptionDialog.normalFail("Connection failed!", "Failed to connect to " + addr + ":" + port, conbuilder.getFailure(), log);
				return;
			case Completed:
				break;
			default:
				throw new IllegalStateException();
		}
		
		ISocket connection = conbuilder.getConnection();
		HashedPassword password = null;
		String passwordInputTitle = "Enter password!";
		String mod_name = null;
		String mod_uid = null;
		
		handshake_loop:
		while(true) {
			ClientsideHandshakeProtocol handshake = new ClientsideHandshakeProtocol(connection, nickname, password);
		
			switch(new ProtocolProgressDialog("Login...", handshake).showDlg()) {
			case Aborted:
				return;
			case Completed:
				mod_name = handshake.getModName();
				mod_uid = handshake.getModUid();
				
				break handshake_loop;
			case Failed:
				if(handshake.isServesrFull()) {
					JOptionPane.showMessageDialog(null, "Server is full!", "Login failed!", JOptionPane.ERROR_MESSAGE);
					return;
				} else if(handshake.isPasswordIncorrect()) {
					JPanel panel = new JPanel();
					panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
					JPasswordField pf = new JPasswordField();
					panel.add(new JLabel("Server requires a password:"));
					panel.add(pf);
					int ok = JOptionPane.showConfirmDialog(null, panel, passwordInputTitle, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
					
					passwordInputTitle = "Password was wrong!";
					if (ok == JOptionPane.OK_OPTION) {
						password = HashedPassword.fromClean(new String(pf.getPassword()));
					}else{
						return;
					}
				} else {
					ExceptionDialog.normalFail("Login failed!", "Failed to login!", handshake.getFailure(), log);
					return;
				}
				break;
			default:
				throw new IllegalStateException();
			}
		}
		
		
		if(mod_name == null || mod_uid == null) {
			throw new IllegalAccessError();
		}
		
		log.debug("Handshake done successfully!");
		log.info("Server uses mod: " + mod_name);
		
		IModInfo modInfo = modManager.getModInfoByUId(mod_uid);
		
		if(modInfo == null) {
			JOptionPane.showMessageDialog(null, "Mod not available!", "Can not start client!", JOptionPane.ERROR_MESSAGE);
			return;
		}
		

		log.info("Start client!");
		IMod mod = modManager.startMod(modInfo);
		
		currentClient = mod.createClient();
		
		if(currentClient == null) {
			JOptionPane.showMessageDialog(null, "Failed to create the client!", "Can not start client!", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		currentClient.start(getMainSceneFrame(), nickname, connection);
	}

	@Override
	public ILogStream getLogStream() {
		return LOGSTREAM;
	}
	
	private ServerWindow getServerWindow() {
		if(serverWin == null) {
			serverWin = new ServerWindow();
			return serverWin;
		}else{
			return serverWin;
		}
	}
	
	
	private void shutdown() {

		log.info("Application closed!");
	}

	@Override
	public ISceneFrame getMainSceneFrame() {
		return mainWin;
	}	
	
	private Thread getShutdownHook() {
		
		return new Thread() {
			@Override
			public void run() {
				shutdown();
			}
		};
	}

	public static void main(String[] args) {
		
		Application app = new Application(args);
		EventQueue.invokeLater(app);
		
		Runtime.getRuntime().addShutdownHook(app.getShutdownHook());
	}
}
