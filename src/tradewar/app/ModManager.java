package tradewar.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import tradewar.api.IApp;
import tradewar.api.IConfig;
import tradewar.api.IDirectory;
import tradewar.api.IMod;
import tradewar.api.IModInfo;
import tradewar.utils.TWClassLoader;
import tradewar.utils.log.Log;

public class ModManager {
	
	public static final String MODNAME_PATTERN = "\\p{Graph}{5,20}";
	public static final String AUTHOR_PATTERN = "\\p{Graph}{2,20}";
	public static final String DESCRIPTION_PATTERN = "\\p{ASCII}*";
	public static final String MOD_REDIRECTFILE = "mod.file";
	
	private static final String CONFIG_MODLIST = "mod-list";

	private class ExtendedModInfo implements IStartableModInfo {
		private static final long serialVersionUID = 5167038344751835449L;
		
		
		public String name;
		public String description;
		public String author;
		public String version;
		
		public String modPath;
		public String uid;

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

		@Override
		public IMod instantiate() {

			return null;
		}
		
		public String getModPath() {
			return modPath;
		}

		@Override
		public String getUID() {
			return uid;
		}
	}

	private Log log = new Log(Application.LOGSTREAM, "mod-manager");
	private IApp app;
	private ConfigManager configManager;
	
	private IDirectory modDir;
	private IConfig config;
	
	
	private IStartableModInfo[] integratedMods;
	private ArrayList<ExtendedModInfo> modList;
	
	ModManager(IApp app, ConfigManager configManager, IDirectory modDir, IConfig modConfig, IStartableModInfo[] integratedMods) {
		this.app = app;
		this.configManager = configManager;
		this.integratedMods = (integratedMods == null? new IStartableModInfo[0] : integratedMods);
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
		IModInfo[] list = new IModInfo[getModCount()];

		System.arraycopy(integratedMods, 0, list, 0, integratedMods.length);
		
		int target = integratedMods.length;
		for(IModInfo info : modList) {
			list[target++] = info;
		}

		return list;
	}
	
	public IMod startMod(IModInfo modInfo) {
		
		if(modInfo instanceof IStartableModInfo) {
			IStartableModInfo smodInfo = ((IStartableModInfo)modInfo);
			IMod mod = smodInfo.instantiate();
			
			IConfig config = configManager.getConfig("mods/" + smodInfo.getUID());
			
			mod.init(app, config);

			return mod;
		}
		
		log.wtf(modInfo.getName() + " was not a registered mod!");
		return null;
	}
	
	public int getModCount() {
		return modList.size() + integratedMods.length;
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
			log.err("Directory[" + dir.getPath() + "] does not contain the mod class \"" + modMainFile + "\"!");			
			return null;
		}
		
		log.info("Found modfile \"" + dir.getPath(modMainFile) + "\"");
		
		for(ExtendedModInfo modi : modList) {
			if(modi.getModPath().equals(path)) {
				return modi;
			}
		}
		
		log.info("Was not on the list! Gather information...");

		ClassLoader loader = new TWClassLoader(dir);
		Class<?> plainClass = null;
		Class<? extends IMod> mainModClass = null;
		
		try {
			plainClass = loader.loadClass(modMainFile);
			mainModClass = plainClass.asSubclass(IMod.class);

		} catch (ClassNotFoundException e) {
			log.err("Failed to load mod main class!");
			log.excp(e);
			
			return null;
		
		} catch(ClassCastException e) {
			log.crit("Was able to load mod main class, but main class does not implements the tradewar.api.IMod interface!");
			log.excp(e);
			
			return null;
		}	
		
	//	Constructor<? extends IMod> ctors = mainModClass.getConstructors();
		
			
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
