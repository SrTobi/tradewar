package tradewar.api;




public interface IMod {

	public String getName();
	public String getAuthor();
	public String getVersion();
	
	public void init(IApp app);
	public void stop();
}
