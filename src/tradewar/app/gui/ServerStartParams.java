package tradewar.app.gui;

import tradewar.api.IModInfo;
import tradewar.api.IServerStartParams;
import tradewar.utils.HashedPassword;

class ServerStartParams implements IServerStartParams {

	
	private int gameServerPort;
	private int queryServerPort;
	private String serverName;
	private HashedPassword serverPassword;
	private int maxPlayer;
	private IModInfo modinfo;
	
	
	public ServerStartParams(ServerCreationDialog dlg) {
		gameServerPort = dlg.getGameServerPort();
		queryServerPort = dlg.getQueryServerPort();
		serverName = dlg.getServerName();
		
		String enteredPassword = dlg.getServerPassword();
		if(enteredPassword == null || enteredPassword.isEmpty()) {
			serverPassword = null;
		} else {
			serverPassword = HashedPassword.fromHash(enteredPassword);
		}
		maxPlayer = dlg.getMaxPlayer();
		
		modinfo = dlg.getSelectedMod();
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
	public byte[] getHashedServerPassword() {
		if(serverPassword == null) {
			return null;
		} else {
			return serverPassword.getHashedPassword();
		}
	}

	@Override
	public int getMaxPlayer() {
		return maxPlayer;
	}

	@Override
	public IModInfo getMod() {
		return modinfo;
	}

}
