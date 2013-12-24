package tradewar.app.mods.classic.client;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import tradewar.api.IScene;
import tradewar.utils.log.Log;

public class GameScene extends JPanel implements IScene {

	private static final long serialVersionUID = -4570236601814288334L;


	private Log log = new Log("game-scene");
	private Client client;
	private ClientModel model;
	
	private EconomyScreen economyScreen;
	
	/**
	 * Create the panel.
	 */
	public GameScene(Client client, ClientModel model) {
		
		if(client == null || model == null) {
			throw new NullPointerException();
		}
		
		this.client = client;
		this.model = model;
		
		setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.7);
		add(splitPane, BorderLayout.CENTER);
		
		economyScreen = new EconomyScreen(model);
		splitPane.setLeftComponent(economyScreen);
	}

	@Override
	public Component getView() {
		return this;
	}

	@Override
	public String getSceneName() {
		return "game-scene";
	}

	@Override
	public String getSceneTitle() {
		return "Tradewar";
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
