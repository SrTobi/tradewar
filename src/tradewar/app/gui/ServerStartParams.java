package tradewar.app.gui;

import tradewar.api.IServerStartParams;

class ServerStartParams implements IServerStartParams {

	
	private int gameServerPort;
	private int queryServerPort;
	private String serverName;
	private String serverPassword;
	private int maxPlayer;
	
	
	public ServerStartParams(ServerCreationDialog dlg) {
		gameServerPort = dlg.getGameServerPort();
		queryServerPort = dlg.getQueryServerPort();
		serverName = dlg.getServerName();
		serverPassword = dlg.getServerPassword();
		maxPlayer = dlg.getMaxPlayer();
	}

	@Override
	public int getGameServerPort() {
		return gameServerPort;
	}

	@Override
	public int getQueryServerPort() {
		return queryServerPort;
	}

	@Override
	public String getServerName() {
		return serverName;
	}

	@Override
	public String getServerPassword() {
		return serverPassword;
	}

	@Override
	public int getMaxPlayer() {
		return maxPlayer;
	}

}
