package tradewar.app.mods.classic.packets;

import tradewar.api.IPacket;
import tradewar.app.FormatPatterns;

public class SendChatMessagePacket implements IPacket {

	private static final long serialVersionUID = -3397975200976692645L;

	private static final String SENDER_FORMAT = FormatPatterns.NICKNAME;
	private static final String MESSAGE_FORMAT = "[\\p{Blank}\\p{Graph}\\p{IsAlphabetic}]{1,100}";

	public final boolean msgByServer;
	public final String sender;
	public final String message;
	
	public SendChatMessagePacket(boolean byServer, String sender, String message) {
		msgByServer = byServer;
		this.sender = sender;
		this.message = message;
	}
	
	@Override
	public long getPacketId() {
		return serialVersionUID;
	}

	@Override
	public boolean check() {
		return sender != null && sender.matches(SENDER_FORMAT)
			&& message != null && message.matches(MESSAGE_FORMAT);
	}

}
