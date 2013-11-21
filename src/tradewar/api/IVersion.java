package tradewar.api;

import java.io.Serializable;

public interface IVersion extends Serializable {

	public boolean isNewerThen(IVersion other);
	public boolean isCompatible(IVersion other);
	
	public String getProductName();
	public String getVersionCode();
}
