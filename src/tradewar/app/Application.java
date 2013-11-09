package tradewar.app;


import tradewar.api.IApp;
import tradewar.app.gui.ApplicationWindow;
import tradewar.app.gui.TestScene;
import tradewar.log.ILogStream;
import tradewar.log.Log;

public class Application implements IApp {

	private Log log = new Log(ILogStream.sys, "app");
	ApplicationWindow appWin;
	
	public Application() {

	}
	
	public void start(String[] args) {
		log.info("Start application...");
		
		appWin = new ApplicationWindow(log.getStream(), "MainWindow");
		appWin.setScene(new TestScene(appWin));
	}
	
	public void run() {
		appWin.setVisible(true);
	}
	
	
	public static void main(String[] args) {
		Application app = new Application();
		
		app.start(args);
		app.run();
	}

}
