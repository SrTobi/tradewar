package tradewar.api;

public interface IQueryServer {
	
	public void setServerAddress(String addr);
	
	public void setActive(boolean active);
	public boolean isActive();
}
