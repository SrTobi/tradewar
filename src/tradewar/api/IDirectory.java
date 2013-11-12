package tradewar.api;

import java.io.InputStream;
import java.io.OutputStream;

public interface IDirectory {
	
	enum FileType {
		File,
		Directory
	}

	public boolean exists(String path);
	public FileType getType(String path);
	
	public String[] getEntries(String path);
	public IDirectory getSubDirectory(String path);
	
	public InputStream readFile(String path);
	public OutputStream writeFile(String path);
	public OutputStream appendFile(String path);
}
