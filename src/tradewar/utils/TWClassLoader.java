package tradewar.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import tradewar.api.IDirectory;

public class TWClassLoader extends ClassLoader {

	IDirectory directory;
	
	public TWClassLoader(IDirectory directory) {
		this.directory = directory;
	}

	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException{
		
		InputStream in = null;
		try {
			in = directory.readFile(name.replace('.', '/'));
		
			byte[] data = readFully(in);
			
			return defineClass(name, data, 0, data.length);
		} catch (IOException e) {
				
			throw new ClassNotFoundException("Failed to find class \"" + name + "\"");
		} finally {
			if(in != null)
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	public static byte[] readFully(InputStream input) throws IOException {
	    
		byte[] buffer = new byte[8192];
	    
	    int bytesRead;
	    ByteArrayOutputStream output = new ByteArrayOutputStream();
	    while ((bytesRead = input.read(buffer)) != -1) {
	        output.write(buffer, 0, bytesRead);
	    }
	    
	    return output.toByteArray();
	}
}
