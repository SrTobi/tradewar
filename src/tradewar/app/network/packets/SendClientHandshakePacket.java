package tradewar.app.network.packets;

import tradewar.api.IPacket;
import tradewar.app.FormatPatterns;
import tradewar.utils.HashedPassword;

public class SendClientHandshakePacket implements IPacket {

	private static final long serialVersionUID = -3967401368072892148L;


	public final String nickname;
	public final byte[] hashedPassword;
	
	
	
	
	public SendClientHandshakePacket(String nickname, HashedPassword hashedPassword) {
		this.nickname = nickname;
		this.hashedPassword = hashedPassword == null ? null : hashedPassword.getHashedPassword();
	}
	
	
	@Override
	public long getPacketId() {
		return serialVersionUID;
	}

	@Override
	public boolean check() {

		return	nickname != null && nickname.matches(FormatPatterns.NICKNAME);
	}
}
