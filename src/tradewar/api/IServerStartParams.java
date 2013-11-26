package tradewar.api;

public interface IServerStartParams {

	public int getGameServerPort();
	public int getQueryServerPort();
	public String getServerName();
	public byte[] getHashedServerPassword();
	public IModInfo getMod();
	public int getMaxPlayer();
}
