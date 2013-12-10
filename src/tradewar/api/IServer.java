package tradewar.api;

import java.io.PrintStream;

public interface IServer {

	public int getPlayerCount();
	
	public void start(PrintStream terminal, ISceneFrame frame);
	public void stop();
	
	public void onTextCommand(String cmd);
	public void onNewPlayer(ISocket socket, String nickname);
}
