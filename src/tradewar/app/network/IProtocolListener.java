package tradewar.app.network;


public interface IProtocolListener {

	public void onProtocolFail(Exception failure);
	public void onProtocolCompleteness();
	public void onProtocolAbort();
}
