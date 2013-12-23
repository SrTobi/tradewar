package tradewar.app.mods.classic;

import java.io.PrintStream;

import tradewar.api.ISceneFrame;
import tradewar.api.ISocket;

public interface IServerState {

	public int getPlayerCount();
	
	public void stop();
	
	public void onTextCommand(String cmd);
	public void onNewPlayer(ISocket socket, String nickname);
}
