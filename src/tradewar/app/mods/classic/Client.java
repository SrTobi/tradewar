package tradewar.app.mods.classic;

import tradewar.api.IApp;
import tradewar.api.IClient;
import tradewar.api.ISceneFrame;
import tradewar.api.ISocket;
import tradewar.app.mods.classic.gui.GameScene;
import tradewar.app.mods.classic.gui.LobbyScene;

public class Client implements IClient {

	private IApp app;
	private String nickname;
	private ISocket connection;
	ISceneFrame frame;
	private LobbyScene lobby;
	private GameScene game;
	
	
	Client(IApp app) {
		this.app = app;
	}
	
	@Override
	public void start(ISceneFrame frame, String nickname, ISocket connection) {

		this.nickname = nickname;
		this.connection = connection;
		this.frame = frame;
		
		this.lobby = new LobbyScene(this, app.getLogStream(), nickname, connection);
		
		frame.setScene(lobby);
	}
	
	public void startGame(int money, String[] stockNames, int[] initialStockValues) {
		game = new GameScene(this, connection, money, stockNames, initialStockValues);
		
		frame.setScene(game);
	}

	@Override
	public void stop() {
		
	}
}
