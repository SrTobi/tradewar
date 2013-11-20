package tradewar.api;

import java.io.IOException;
import java.net.Socket;

public interface ISocket {
	
	public Socket getUnderlyingSocket();
	
	public void send(IPacket packet) throws IOException;
	public IPacket receive() throws IOException;
	public void close();
}
