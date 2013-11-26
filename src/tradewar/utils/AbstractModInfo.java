package tradewar.utils;

import tradewar.api.IModInfo;

public abstract class AbstractModInfo implements IModInfo {

	
	private String uid = null;

	@Override
	public String getUId() {
		
		if(uid == null) {
			// Build string and then hash it
			uid = Hasher.hashString(getName() + getAuthor() + getVersion());
		}
		
		return uid;
	}
}
