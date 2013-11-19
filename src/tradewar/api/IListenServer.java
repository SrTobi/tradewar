package tradewar.api;

public interface IListenServer {

	public void poll();
	public void listen(boolean listening);
	public boolean isListening();
}
