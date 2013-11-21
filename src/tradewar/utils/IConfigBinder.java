package tradewar.utils;

import tradewar.api.IConfig;

public interface IConfigBinder {
	public void load();
	public IConfig save();
}
