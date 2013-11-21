package tradewar.api;

import java.io.IOException;
import java.net.Socket;

public interface ISocket {
	
	public Socket getUnderlyingSocket();
	
	public void addSocketListener(ISocketListener listener);
	public void removeSocketListener(ISocketListener listener);
	
	public void send(IPacket packet) throws IOException;
	public boolean hasNewPacket();
	public IPacket nextPacket();
	public IPacket waitForPacket();
	public IPacket waitForPacket(int timeout);
	
	public void close();
}
