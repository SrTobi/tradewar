package tradewar.utils;

import tradewar.app.IStartableModInfo;

public abstract class AbstractInnerModInfo extends AbstractModInfo implements IStartableModInfo {

	private final String name;
	private final String description;
	private final String author;
	private final String version;
	
	
	public AbstractInnerModInfo(String name, String description, String author, String version) {
		this.name = name;
		this.description = description;
		this.author = author;
		this.version = version;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getAuthor() {
		return author;
	}

	@Override
	public String getVersion() {
		return version;
	}

}
