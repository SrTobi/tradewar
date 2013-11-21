package tradewar.api;

import java.io.IOException;

public interface ISocketListener {

	public void onReceive(IPacket packet);
	public void onSend(IPacket packet);
	
	public void onError(IOException e);
	public void onDisconnect();	
}
