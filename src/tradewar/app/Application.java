package tradewar.app;


import tradewar.api.IApp;
import tradewar.log.ILogStream;
import tradewar.log.Log;

public class Application implements IApp {

	private Log log = new Log(ILogStream.sys, "app");
	
	
	public Application() {

	}
	
	public void start(String[] args) {
		log.info("Start application...");
	}
	
	public void run() {
		log.wtf("Not implemented!");
	}
	
	
	public static void main(String[] args) {
		Application app = new Application();
		
		app.start(args);
		app.run();
	}

}
