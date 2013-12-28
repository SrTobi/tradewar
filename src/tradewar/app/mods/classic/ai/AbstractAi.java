package tradewar.app.mods.classic.ai;

import tradewar.api.IPacket;
import tradewar.api.ISocket;

public class AbstractAi implements Runnable {

	private ISocket connection;
	private String nickname;
	private int turnsPerMinute;
	private Thread aiThread;
	
	public AbstractAi(ISocket connection, String nickname, int turnsPerMinute) {
		this.connection = connection;
		this.nickname = nickname;
		this.turnsPerMinute = turnsPerMinute;
		
		aiThread = new Thread(this);
	}
	
	public void start() {
		aiThread.start();
	}
	
	protected void send(IPacket packet) {
		connection.send(packet);
	}

	@Override
	public final void run() {
		
	}
	
	protected void runAi() {
		
	}
}
