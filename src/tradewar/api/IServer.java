package tradewar.api;

public interface IServer {

	public int getPlayerCount();
	
	public void start(ISceneFrame frame);
	public void stop();
}
