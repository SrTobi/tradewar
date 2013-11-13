package tradewar.api;

import java.io.Serializable;

public interface IModInfo extends Serializable {
	
	public String getName();
	public String getDescription();
	public String getAuthor();
	public String getVersion();
}
