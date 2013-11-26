package tradewar.app.network.packets;

import tradewar.api.IModInfo;
import tradewar.api.IPacket;
import tradewar.api.IServerStartParams;
import tradewar.api.IVersion;
import tradewar.app.FormatPatterns;

public class SendServerHandshakePacket implements IPacket {

	private static final long serialVersionUID = 3869593826617239214L;

	
	public final IVersion serverAppVersion;
	public final boolean passwordCorrect;
	public final boolean tooManyPlayer;
	public final boolean accepted;
	public final String mod_name;
	public final String mod_uid;
	
	
	public SendServerHandshakePacket(IVersion version, boolean passwordCorrect, boolean tooManyPlayer, IServerStartParams ssparams) {
		this.serverAppVersion = version;
		this.passwordCorrect = passwordCorrect;
		this.tooManyPlayer = tooManyPlayer;
		
		
		IModInfo modinfo = ssparams.getMod();
		
		this.mod_name = modinfo.getName();
		this.mod_uid = modinfo.getUId();
		 
		this.accepted = passwordCorrect && !tooManyPlayer;
	}
	
	@Override
	public long getPacketId() {
		return serialVersionUID;
	}

	@Override
	public boolean check() {
		return serverAppVersion != null
			&& accepted == (passwordCorrect && !tooManyPlayer)
			&& mod_name != null && mod_name.matches(FormatPatterns.MODNAME)
			&& mod_uid != null && mod_uid.matches(FormatPatterns.MOD_UID);
	}
}
