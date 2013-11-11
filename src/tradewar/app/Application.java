package tradewar.app;


import java.awt.EventQueue;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import tradewar.api.IApp;
import tradewar.api.ISceneFrame;
import tradewar.app.gui.ApplicationWindow;
import tradewar.app.gui.LauncherScene;
import tradewar.utils.log.ILogStream;
import tradewar.utils.log.Log;

public class Application implements IApp, Runnable {

	private static int STANDARD_GAMESERVER_PORT = 23451;
	private static int STANDARD_QUERYSERVER_PORT = 23452;
	
	private Log log = new Log(ILogStream.sys, "app");
	ApplicationWindow mainWin;
	
	public Application(String[] args) {

	}

	@Override
	public void run() {

		log.info("Start application...");
		
        try {
        	log.debug("Setup look and feel...");
			UIManager.setLookAndFeel(
			    UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			log.err("Failed to set look and feel!");
			log.excp(e);
		}
		
		mainWin = new ApplicationWindow(log.getStream(), "MainWindow");
		mainWin.setScene(new LauncherScene(log.getStream(), this, STANDARD_GAMESERVER_PORT, STANDARD_QUERYSERVER_PORT));
		
		mainWin.setVisible(true);
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
