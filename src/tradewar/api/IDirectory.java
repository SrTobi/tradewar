package tradewar.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface IDirectory {
	
	public static enum FileType {
		None,
		File,
		Directory,
		Unknown
	}

	public boolean canReadMultipleFiles();
	public boolean canWriteMultipleFiles();
	
	public String getPath();
	public String getPath(String entity);
	
	public boolean exists(String path);
	public FileType getType(String path);
	
	public String[] getEntries();
	public String[] getEntries(String path);
	public IDirectory getSubDirectory(String path);
	public IDirectory getSubDirectory(String path, boolean create);
	
	public InputStream readFile(String path) throws IOException;
	public OutputStream writeFile(String path) throws IOException;
	public OutputStream appendFile(String path) throws IOException;
}
