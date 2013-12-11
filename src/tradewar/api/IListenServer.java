package tradewar.api;

public interface IListenServer {

	public void listen(boolean listening);
	public boolean isListening();
	
	public int getPlayerCount();
	public int getConnectedCount();
}
