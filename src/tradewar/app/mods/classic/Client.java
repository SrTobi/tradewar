package tradewar.app.mods.classic;

import tradewar.api.IApp;
import tradewar.api.IClient;
import tradewar.api.ISceneFrame;
import tradewar.api.ISocket;
import tradewar.app.mods.classic.gui.LobbyScene;

public class Client implements IClient {

	private IApp app;
	private String nickname;
	private ISocket connection;
	private LobbyScene lobby;
	
	
	Client(IApp app) {
		this.app = app;
	}
	
	@Override
	public void start(ISceneFrame frame, String nickname, ISocket connection) {

		this.nickname = nickname;
		this.connection = connection;
		
		this.lobby = new LobbyScene(app.getLogStream(), nickname, connection);
		
		frame.setScene(lobby);
	}

	@Override
	public void stop() {
		
	}
}
