package tradewar.app.network.packets;

import tradewar.api.IPacket;
import tradewar.api.IVersion;

public class SendServerHandshakePacket implements IPacket {

	private static final long serialVersionUID = 3869593826617239214L;

	
	public final IVersion serverAppVersion;
	public final boolean passwordCorrect;
	public final boolean tooManyPlayer;
	public final boolean accepted;
	
	
	
	public SendServerHandshakePacket(IVersion version, boolean passwordCorrect, boolean tooManyPlayer) {
		this.serverAppVersion = version;
		this.passwordCorrect = passwordCorrect;
		this.tooManyPlayer = tooManyPlayer;
		
		this.accepted = passwordCorrect && !tooManyPlayer;
	}	
	
	@Override
	public long getPacketId() {
		return serialVersionUID;
	}

	@Override
	public boolean check() {
		return serverAppVersion != null
			&& accepted == (passwordCorrect && !tooManyPlayer);
	}
}
