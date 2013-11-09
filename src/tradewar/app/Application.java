package tradewar.app;


import java.awt.EventQueue;

import tradewar.api.IApp;
import tradewar.app.gui.ApplicationWindow;
import tradewar.app.gui.TestScene;
import tradewar.log.ILogStream;
import tradewar.log.Log;

public class Application implements IApp, Runnable {

	private Log log = new Log(ILogStream.sys, "app");
	ApplicationWindow appWin;
	
	public Application(String[] args) {

	}

	public void run() {

		log.info("Start application...");
		
		appWin = new ApplicationWindow(log.getStream(), "MainWindow");
		appWin.setScene(new TestScene(appWin));
		
		appWin.setVisible(true);
	}
	

	private void shutdown() {

		log.info("Application closed!");
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
