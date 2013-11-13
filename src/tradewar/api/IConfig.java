package tradewar.api;

import java.io.Serializable;

public interface IConfig {

	public <E extends Serializable> E get(String id, Class<? extends E> clazz);
	public <E extends Serializable> E get(String id, E defaultValue);
	public <E extends Serializable> void set(String id, E value);
	
	public void save();
	
	public void setAutoSave(boolean autoSave);
	public boolean getAutoSave();
}
