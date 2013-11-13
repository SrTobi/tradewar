package tradewar.app;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.jar.JarInputStream;

import tradewar.api.IConfig;
import tradewar.api.IDirectory;
import tradewar.api.IModInfo;
import tradewar.utils.log.Log;

public class ModManager {
	
	public static final String MODNAME_PATTERN = "\\p{Graph}{5,20}";
	public static final String AUTHOR_PATTERN = "\\p{Graph}{2,20}";
	public static final String DESCRIPTION_PATTERN = "\\p{ASCII}*";
	public static final String MOD_REDIRECTFILE = "mod.file";
	
	private static final String CONFIG_MODLIST = "mod-list";

	private class ExtendedModInfo implements IModInfo {		
		public String name;
		public String description;
		public String author;
		public String version;
		
		public String modPath;

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
		
		public String getModPath() {
			return modPath;
		}
	}

	private Log log = new Log(Application.LOGSTREAM, "mod-manager");
	private IDirectory modDir;
	private IConfig config;
	
	private ArrayList<ExtendedModInfo> modList;
	
	ModManager(IDirectory modDir, IConfig modConfig) {
		this.modDir = modDir;
		this.config = modConfig;
		
		config.setAutoSave(false);
		
		try {
			loadModsFromConfigList();
		} catch (IOException e) {
			log.warn(e.getMessage());
			modList = new ArrayList<ExtendedModInfo>();
		}

		buildModListFromModDirectory();
	}
	
	public IModInfo[] getModInfos() {
		return modList.toArray(new IModInfo[getModCount()]);
	}
	
	public int getModCount() {
		return modList.size();
	}
	
	@SuppressWarnings("unchecked")
	private void loadModsFromConfigList() throws IOException {
		ArrayList<?> newList = config.get(CONFIG_MODLIST, ArrayList.class);
		
		if(newList != null) {
			for(Object o : newList) {
				if(!(o instanceof ExtendedModInfo)) {
					newList = null;
					break;
				}
			}
		}
		
		if(newList == null) {
			throw new IOException("Failed to load mods from condig");
		}
		
		modList = (ArrayList<ExtendedModInfo>)newList;
	}
	
	private void buildModListFromModDirectory() {

		log.info("Build mod list from mod directory...");
		ArrayList<ExtendedModInfo> list = examineModPath();
		
		log.info(list.size() + " mods were found!");
		
		modList = list;
		config.set(CONFIG_MODLIST, modList);
		config.save();
	}
	
	private ArrayList<ExtendedModInfo> examineModPath() {
		
		ArrayList<ExtendedModInfo> list = new ArrayList<>();
		String[] entries = modDir.getEntries();
		
		log.info("Examine directory \"" + modDir.getPath() + "\"");
		log.info("Found " + entries.length + " entities");
		
		for(String entry : entries) {
			
			IDirectory.FileType type = modDir.getType(entry);
			
			switch(type) {
			case File:
				log.warn("Mod folder should not contain files!");
				break;
				
			case Directory:
				ExtendedModInfo modi = examinePathForMod(modDir.getSubDirectory(entry));
				
				if(modi != null)
					list.add(modi);
				break;
				
			case Unknown:
				log.warn("Type of \"" + modDir.getPath(entry) + "\" is unknown!");
				break;
			case None:
			default:
				log.err("\"" + modDir.getPath(entry) + "\" is not an entity? Continue...");
				break;
			}
		}
		
		return list;
	}
	
	private ExtendedModInfo examinePathForMod(IDirectory dir) {
		
		String path = dir.getPath();
		
		String modMainFile = findModMainClassFilePath(dir);
		
		if(modMainFile == null) {		
			return null;
		}
		
		if(!dir.exists(modMainFile.replace('.', '/'))) {
			log.err("Directory does not contain the mod class \"" + modMainFile + "\"!");			
			return null;
		}
		
		log.info("Found modfile \"" + dir.getPath(modMainFile) + "\"");
		
		for(ExtendedModInfo modi : modList) {
			if(modi.getModPath().equals(path)) {
				return modi;
			}
		}
		
		log.info("Was not on the list! Gather information...");
		
		return null;
	}
	
	private String findModMainClassFilePath(IDirectory dir) {

		if(!dir.exists(MOD_REDIRECTFILE)) {
			log.err("Mod directory[" + dir.getPath() + "] does not contain the redirect file (" + MOD_REDIRECTFILE + ")");
			return null;
		}

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(dir.readFile(MOD_REDIRECTFILE)));
			
			return in.readLine();
		} catch (IOException e) {
			log.err("Failed to read " + dir.getPath(MOD_REDIRECTFILE));
		}
		
		return null;
	}
	
	/*private ExtendedModInfo extractModInfo(IDirectory dir, String path) {
		//JarInputStream jarStream = new JarInputStream(dir.readFile(path));
	}*/
}
