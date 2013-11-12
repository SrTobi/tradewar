package tradewar.api;

public interface IConfig {

	public <E> E get(String id, Class<E> clazz);
	public <E> E get(String id, E defaultValue);
	public <E> void set(String id, E value);
	
	public void reload();
	public void save();
	
	public void setAutoSave(boolean autoSave);
	public boolean getAutoSave();
}
