package tradewar.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tradewar.api.IConfig;

public class ConfigBinding {
	
	private List<IConfigBinder> binders = new ArrayList<IConfigBinder>();

	public void addBinder(IConfigBinder binder) {
		binders.add(binder);
	}

	public void removeBinder(IConfigBinder binder) {
		binders.remove(binder);
	}
	
	public void load() {
		for(IConfigBinder binder : binders) {
			binder.load();
		}
	}

	public void save() {
		Set<IConfig> configs = new HashSet<>();
		for(IConfigBinder binder : binders) {
			configs.add(binder.save());
		}

		for(IConfig c : configs) {
			if(c != null) {
				c.save();
			}
		}
	}
}
