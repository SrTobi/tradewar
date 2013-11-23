package tradewar.app.network.packets;

import tradewar.api.IPacket;
import tradewar.app.FormatPatterns;
import tradewar.utils.HashedPassword;

public class SendClientHandshakePacket implements IPacket {

	private static final long serialVersionUID = -3967401368072892148L;


	public final String nickname;
	public final HashedPassword hashedPassword;
	
	
	
	
	public SendClientHandshakePacket(String nickname, String clearPassword) {
		this.nickname = nickname;
		this.hashedPassword = new HashedPassword(clearPassword);
	}
	
	
	@Override
	public long getPacketId() {
		return serialVersionUID;
	}

	@Override
	public boolean check() {

		return	nickname != null && nickname.matches(FormatPatterns.NICKNAME)
			 && hashedPassword != null;
	}
}
