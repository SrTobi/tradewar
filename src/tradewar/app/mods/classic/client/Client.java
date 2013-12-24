package tradewar.app.mods.classic.client;

import tradewar.api.IApp;
import tradewar.api.IClient;
import tradewar.api.ISceneFrame;
import tradewar.api.ISocket;

public class Client implements IClient {

	private IApp app;
	private String nickname;
	private ISocket connection;
	ISceneFrame frame;
	private LobbyScene lobby;
	private GameScene game;
	
	
	public Client(IApp app) {
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
	
	public void startGame(ClientModel model) {
		game = new GameScene(this, model);
		
		new ClientNetworkBinding(model, connection);
		
		frame.setScene(game);
	}

	@Override
	public void stop() {
		
	}
}
