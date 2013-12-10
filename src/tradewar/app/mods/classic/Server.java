package tradewar.app.mods.classic;

import java.io.PrintStream;

import tradewar.api.ISceneFrame;
import tradewar.api.IServer;
import tradewar.api.ISocket;

public class Server implements IServer {

	
	PrintStream output;
	
	@Override
	public int getPlayerCount() {
		return 0;
	}
	
	
	@Override
	public void start(PrintStream terminal, ISceneFrame frame) {
		output = terminal;
	}

	@Override
	public void stop() {		
	}

	@Override
	public void onNewPlayer(ISocket socket, String nickname) {

	}


	@Override
	public void onTextCommand(String cmd) {
		output.println("Commands are not supported at this time!");
	}

}
