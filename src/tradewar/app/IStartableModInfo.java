package tradewar.app;

import tradewar.api.IMod;
import tradewar.api.IModInfo;

public interface IStartableModInfo extends IModInfo {

	public IMod instantiate();
}
