package tradewar.api;




public interface IMod {

	public String getName();
	public String getAuthor();
	public String getVersion();
	
	public void init(IApp app, IConfig config);
	public void stop();
	
	public boolean hasDedicatedServer();
	public boolean hasClientSideServerContol();
	
	public IClient createClient();
	public IServer createDedicatedServer(IServerStartParams params, IListenServer listenServer, IQueryServer queryServer);
}
