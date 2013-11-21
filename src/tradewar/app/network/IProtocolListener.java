package tradewar.app.network;

public interface IProtocolListener<Protocol> {

	public void onProtocolFail(Protocol protocol);
	public void onProtocolCompleted(Protocol protocol);
}
