package tradewar.app.mods.classic;

import tradewar.api.IServer;
import tradewar.api.ISocket;

public abstract class StateServer implements IServer {

	private IServerState state;
	
	public StateServer() {
		this.state = null;
	}
	
	public StateServer(IServerState state) {

		this.state = state;
	}
	
	public void setState(IServerState state) {
		if(state == null) {
			throw new NullPointerException();
		}
		this.state = state;
	}

	@Override
	public int getPlayerCount() {
		checkState();
		return state.getPlayerCount();
	}

	@Override
	public void stop() {
		checkState();
		state.stop();
	}

	@Override
	public void onTextCommand(String cmd) {
		checkState();
		state.onTextCommand(cmd);
	}

	@Override
	public void onNewPlayer(ISocket socket, String nickname) {
		checkState();
		state.onNewPlayer(socket, nickname);
	}

	private void checkState() {
		if(state == null) {
			throw new IllegalStateException();
		}
	}
}
