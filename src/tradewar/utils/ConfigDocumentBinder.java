package tradewar.utils;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import tradewar.api.IConfig;

public class ConfigDocumentBinder implements IConfigBinder {
	
	private Document document;
	private String configId;
	private IConfig config;
	
	public ConfigDocumentBinder(IConfig config, String id, Document document) {
		this.document = document;
		this.configId = id;
		this.config = config;
	}

	@Override
	public void load() {
		
		String value;
		
		value = config.get(configId, String.class);
		
		if(value != null) {
			try {
				document.remove(0, document.getLength());
				document.insertString(0, value, null);
			} catch (BadLocationException e) {
			}
		}
	}

	@Override
	public IConfig save() {
		try {
			config.set(configId, document.getText(0, document.getLength()));
		} catch (BadLocationException e) {
		}
		
		return config;
	}
}
