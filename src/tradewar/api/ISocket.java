package tradewar.api;

import java.net.Socket;

import javax.naming.OperationNotSupportedException;

public interface ISocket {
	
	public Socket getUnderlyingSocket() throws OperationNotSupportedException;
	public boolean isConnected();
	
	public void addSocketListener(ISocketListener listener);
	public void removeSocketListener(ISocketListener listener);
	
	public void send(IPacket packet);
	public boolean hasNewPacket();
	public IPacket nextPacket();
	public IPacket waitForPacket();
	public IPacket waitForPacket(int timeout);
	
	public void close();
}
