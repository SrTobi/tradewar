package tradewar.api;

public interface IServerStartParams {

	public int getGameServerPort();
	public int getQueryServerPort();
	public String getServerName();
	public String getServerPassword();
	public int getMaxPlayer();
}
