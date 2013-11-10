package tradewar.app;


import java.awt.EventQueue;

import tradewar.api.IApp;
import tradewar.api.ISceneFrame;
import tradewar.app.gui.ApplicationWindow;
import tradewar.app.gui.LauncherScene;
import tradewar.utils.log.ILogStream;
import tradewar.utils.log.Log;

public class Application implements IApp, Runnable {

	private Log log = new Log(ILogStream.sys, "app");
	ApplicationWindow mainWin;
	
	public Application(String[] args) {

	}

	@Override
	public void run() {

		log.info("Start application...");
		
		mainWin = new ApplicationWindow(log.getStream(), "MainWindow");
		mainWin.setScene(new LauncherScene(log.getStream(), this));
		
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
