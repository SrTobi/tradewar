package tradewar.api;

import java.io.Serializable;

public interface IPacket extends Serializable {

	public long getPacketId();
	public boolean check();
}
