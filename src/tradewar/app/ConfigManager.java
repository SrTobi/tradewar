package tradewar.app;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import tradewar.api.IConfig;
import tradewar.api.IDirectory;
import tradewar.utils.log.Log;

public class ConfigManager {

	public final boolean DEFAULT_AUTOSAVE = true;
	
	private Log log = new Log(Application.LOGSTREAM, "config-manager");
	private IDirectory configDir;
	private HashMap<String, IConfig> loadedConfigs;
	
	ConfigManager(IDirectory configDir) {
		this.configDir = configDir;
	}
	
	public synchronized IConfig getConfig(String path) {
		
		loadedConfigs = new HashMap<String, IConfig>();
		IConfig config = loadedConfigs.get(path);
		
		if(config == null) {
			config = new Configuration(path);
			loadedConfigs.put(path, config);
		}
		
		return config;
	}
	
	private class Configuration implements IConfig {

		private HashMap<? super Object, ? super Object> values;
		private String configPath;
		private boolean autoSave;
		
		public Configuration(String path) {
		
			this.configPath = path;
			this.autoSave = DEFAULT_AUTOSAVE;
			
			
			if(!load()) {
				values = new HashMap<>();
			}
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public synchronized <E extends Serializable> E get(String id, Class<? extends E> clazz) {

			Object o = values.get(id);
			
			if(clazz.isInstance(o))
				return (E) o;

			return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public synchronized <E extends Serializable> E get(String id, E defaultValue) {

			Object o = values.get(id);
			
			if(defaultValue.getClass().isInstance(o))
				return (E) o;

			return defaultValue;
		}

		@Override
		public synchronized <E extends Serializable> void set(String id, E value) {
			values.put(id, value);
			
			if(getAutoSave()) {
				save();
			}
		}

		@Override
		public synchronized void save() {
			ObjectOutputStream out = null;
			try {
				out = new ObjectOutputStream(configDir.writeFile(configPath));
				out.writeObject(values);
			} catch (IOException e) {
				log.crit("Failed to write config file \"" + configDir.getPath(configPath) + "\"");
				log.excp(e);
			} finally {
				try {
					if(out != null)
						out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void setAutoSave(boolean autoSave) {
			this.autoSave = autoSave;
		}

		@Override
		public boolean getAutoSave() {
			return autoSave;
		}
		
		@SuppressWarnings({ "unchecked" })
		private boolean load() {
			
			log.info("Loading config file \"" + configDir.getPath(configPath) + "\"");

			Object obj;
			ObjectInputStream os = null;
			try {
				try {
					os = new ObjectInputStream(configDir.readFile(configPath));
				} catch (IOException e) {
					log.err("Failed to open config file: " + configPath);
					log.excp(e);
					return false;
				}
				
				try {
					obj = os.readObject();
				} catch (ClassNotFoundException | IOException e) {
					log.err("Failed to read config file: " + configPath);
					log.excp(e);
					return false;
				}
			} finally {
				try {
					if(os != null)
						os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try {
				values = (HashMap<? super Object, ? super Object>)obj;
			}catch(ClassCastException e) {
				log.err("Failed to read expected datastructure!");
				log.excp(e);
				return false;
			}
			
			return true;
		}
		
	}

}
