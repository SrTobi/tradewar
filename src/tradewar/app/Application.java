package tradewar.app;


import java.awt.EventQueue;
import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import tradewar.api.IApp;
import tradewar.api.IConfig;
import tradewar.api.IDirectory;
import tradewar.api.ILogStream;
import tradewar.api.ISceneFrame;
import tradewar.app.gui.ApplicationWindow;
import tradewar.app.gui.LauncherScene;
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


	@Override
	public ILogStream getLogStream() {
		return LOGSTREAM;
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
