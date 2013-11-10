package tradewar.app.gui;

import java.awt.Component;

import javax.swing.JPanel;

import tradewar.api.IApp;
import tradewar.api.IScene;
import tradewar.utils.log.ILogStream;
import tradewar.utils.log.Log;
import net.miginfocom.swing.MigLayout;

public class LauncherScene extends JPanel implements IScene {

	private static final long serialVersionUID = 4078132421255645474L;

	private Log log;
	private IApp app;
	
	
	/**
	 * Create the panel.
	 */
	public LauncherScene(ILogStream logStream, IApp app) {

		this.log = new Log(logStream, "launcher-scene");
		this.app = app;
		
		setup();
	}
	
	private void setup() {

		setLayout(new MigLayout("", "[grow]", "[grow]"));
		
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

}
