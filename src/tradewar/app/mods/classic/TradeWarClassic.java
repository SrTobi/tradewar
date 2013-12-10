package tradewar.app.mods.classic;

import tradewar.api.IApp;
import tradewar.api.IClient;
import tradewar.api.IConfig;
import tradewar.api.IListenServer;
import tradewar.api.IMod;
import tradewar.api.IQueryServer;
import tradewar.api.IServer;
import tradewar.api.IServerStartParams;
import tradewar.app.IStartableModInfo;
import tradewar.utils.AbstractInnerModInfo;

public class TradeWarClassic implements IMod {
	
	public static final String MODINFO_NAME = "TradeWar Classic";
	public static final String MODINFO_AUTHOR = "Tobias Kahlert";
	public static final String MODINFO_DESC = "A remake of the famous original TradeWar gameplay";
	public static final String MODINFO_VERSION = "0.0.0.1";
	public static final String MODINFO_UID = "Tradewar-Classic";

	public static final IStartableModInfo INST = new AbstractInnerModInfo(MODINFO_NAME, MODINFO_DESC, MODINFO_AUTHOR, MODINFO_VERSION) {
		
		@Override
		public IMod instantiate() {
			return new TradeWarClassic();
		}
	};
	
	private TradeWarClassic() {
		
	}

	@Override
	public String getName() {
		return MODINFO_NAME;
	}

	@Override
	public String getAuthor() {
		return MODINFO_AUTHOR;
	}

	@Override
	public String getVersion() {
		return MODINFO_VERSION;
	}

	@Override
	public void init(IApp app, IConfig config) {
		
	}

	@Override
	public void stop() {
	}

	@Override
	public boolean hasDedicatedServer() {
		return true;
	}

	@Override
	public boolean hasClientSideServerContol() {
		return false;
	}

	@Override
	public IClient createClient() {
		return null;
	}

	@Override
	public IServer createDedicatedServer(IServerStartParams params, IListenServer listenServer, IQueryServer queryServer) {
		return new Server();
	}

}
