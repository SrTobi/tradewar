package tradewar.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import tradewar.api.IDirectory;
import tradewar.api.ILogStream;
import tradewar.utils.log.Log;

class FileManager {
	
	private static FileManager inst;
	private Log log;
	private IDirectory rootDirectory;

	public FileManager(ILogStream logStream) throws IOException {
		
		log = new Log(logStream, "filemanager");
		
		log.info("Create file manager...");
		
		if(inst != null) {
			throw new IllegalStateException("Can only create one file manager!");
		}
		
		if(IsProgramPackedAsJar()) {
			log.info("Mounting the jar for internal fs.");
			rootDirectory = null;
		} else {
			// absolute path we want to mound
			String path = getProgramPath();
			
			log.info("Mounting the file system for internal fs.");
			log.info("FS path is " + path);
			rootDirectory = new FileSystemDirectory(path);
		}
		
		inst = this;
	}
	
	public static boolean IsProgramPackedAsJar() throws IOException {
		String className = FileManager.class.getName().replace('.', '/');
		String classJar = FileManager.class.getResource("/" + className + ".class").toString();

		if(classJar.startsWith("jar:"))
			return true;
		
		if(classJar.startsWith("file:"))
			return false;
		
		throw new IOException("Failed to determine file system!");
	}
	
	public static String getProgramPath() {

		URI uri = null;
		try {
			uri = new URI(FileManager.class.getResource("/").getPath());

			return uri.getPath();
		} catch (URISyntaxException e) {

			return null;
		}
		
	}

	public IDirectory getRoot() {
		return rootDirectory;
	}
	
	public static String concatinate(String path1, String path2) {

		path1 = path1.replace('\\', '/');
		path2 = path2.replace('\\', '/');
		
		if(!path1.endsWith("/")) {
			return path1 + "/" + path2;
		}
		return path1 + path2;
	}
	
	
	public static String parentDir(String path) {

		path = path.replace('\\', '/');
		
		int idx = path.lastIndexOf('/', path.length() - 2);
		
		return (idx >= 0? path.substring(0, idx + 1) : "");
	}
	
	
	/**
	 * 
	 * @author Tobi
	 * 
	 * FSMounter for the local filesystem
	 *
	 */
	private class FileSystemDirectory implements IDirectory {

		private String root;

		FileSystemDirectory(String path) throws IOException {
			
			path = path.replace('\\', '/');
			
			if(!path.endsWith("/"))
				path = path + "/";
			
			root = path;

			if(!exists(""))
				throw new IOException("Failed to find programs root directory!");
		}


		@Override
		public boolean canReadMultipleFiles() {
			return true;
		}

		@Override
		public boolean canWriteMultipleFiles() {
			return true;
		}
		

		@Override
		public String getPath() {
			return root;
		}
		
		@Override
		public String getPath(String path) {
				
			return root + (path.startsWith("/")? path.substring(1) : path);
		}
		
		@Override
		public boolean exists(String path) {

			return getFile(path).exists();
		}

		@Override
		public FileType getType(String path) {

			File file = getFile(path);
			
			if(!file.exists()) {
				return FileType.None;
				
			} else if(file.isFile()) {
				return FileType.File;
				
			} else if(file.isDirectory()) {
				return FileType.Directory;
				
			} else {
				return FileType.Unknown;
			}
		}


		@Override
		public String[] getEntries() {

			return getEntries("");
		}

		@Override
		public String[] getEntries(String path) {
			String[] fileList = getFile(path).list();
			
			if(fileList == null) {
				log.err("Failed to list directory[" + getPath(path) + "]!");
			}
			
			return fileList;
		}

		@Override
		public IDirectory getSubDirectory(String path) {

			return getSubDirectory(path, false);
		}

		@Override
		public IDirectory getSubDirectory(String path, boolean create) {

			try {
				File dir = getFile(path);
				
				if(create && !dir.exists()) {
					if(!dir.mkdirs())
						throw new IllegalStateException("Failed to create path " + dir.getPath());
				}
				
				return new FileSystemDirectory(dir.getPath());
				
			} catch (IOException e) {

				log.err("Failed to get sub directory[" + getPath(path) + "]");
				return null;
			}
		}

		@Override
		public InputStream readFile(String path) throws IOException {

			try {
				return new FileInputStream(getFile(path));
			} catch (FileNotFoundException e) {
				log.err("Was not able to read " + getPath(path));
				throw e;
			}
		}

		@Override
		public OutputStream writeFile(String path) throws IOException {

			try {
				return new FileOutputStream(getFile(path));
			} catch (FileNotFoundException e) {
				log.err("Was not able to write to " + getPath(path));
				throw e;
			}
		}

		@Override
		public OutputStream appendFile(String path) throws IOException {

			try {
				return new FileOutputStream(getFile(path), true);
				
			} catch (FileNotFoundException e) {
				log.err("Was not able to append to " + getPath(path));
				throw e;
			}
		}

		@Override
		public String toString() {
			return root;
		}
		
		private File getFile(String path) {
			return new File(getPath(path));
		}
	}
	
	/**
	 * 
	 * @author Tobi
	 * 
	 * FSMounter for the jar
	 */
	private class JarFSDirectory implements IDirectory {


		@Override
		public boolean canReadMultipleFiles() {
			return true;
		}

		@Override
		public boolean canWriteMultipleFiles() {
			return false;
		}

		@Override
		public String getPath() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getPath(String entity) {
			// TODO Auto-generated method stub
			return null;
		}		
		
		@Override
		public boolean exists(String path) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public FileType getType(String path) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String[] getEntries() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String[] getEntries(String path) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IDirectory getSubDirectory(String path) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IDirectory getSubDirectory(String path, boolean create) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public InputStream readFile(String path) throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public OutputStream writeFile(String path) throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public OutputStream appendFile(String path) throws IOException {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
