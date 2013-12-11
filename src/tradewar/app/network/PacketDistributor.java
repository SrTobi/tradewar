package tradewar.app.network;

import java.util.Collection;
import java.util.HashMap;

import tradewar.api.ILogStream;
import tradewar.api.IPacket;
import tradewar.app.Application;
import tradewar.utils.log.Log;

public class PacketDistributor {

	private class PacketExecutor<PacketType extends IPacket> {
		private IPacketHandler<PacketType> handler;
		
		public PacketExecutor(IPacketHandler<PacketType> handler) {
			this.handler = handler;
		}
		
		@SuppressWarnings("unchecked")
		public void execute(IPacket packet) throws Exception {
			handler.onPacket((PacketType)packet);
		}
	}
	
	private Log log;
	private HashMap<Class<? extends IPacket>, PacketExecutor<?>> handlers;
	
	public PacketDistributor() {
		this(Application.LOGSTREAM);
	}
	
	public PacketDistributor(ILogStream logstream) {
		this.log = new Log(logstream, "pack-distributor");
		handlers = new HashMap<>();
	}
	
	public <E extends IPacket> void addPacketHandler(IPacketHandler<E> handler) {
		PacketExecutor<?> exec = new PacketExecutor<>(handler);
		if(handlers.put(handler.getPacketClass(), exec) != null) {
			log.debug("Registered packet handler for a packet class that was already registered!");
		}
	}
	
	public void removePacketHandler(IPacketHandler<?> handler) {
		handlers.remove(handler);
	}
	
	public boolean distribute(IPacket packet) throws Exception {

		PacketExecutor<?> exec = handlers.get(packet.getClass());
		
		if(exec == null) {
			log.err("Tried to distribute an unknown Packet!");
			return false;
		}
		
		exec.execute(packet);
		
		return true;
	}
	
	public int distributeAll(Collection<IPacket> packets) throws Exception {
		int num = 0;

		for(IPacket packet : packets) {
			if(distribute(packet)) {
				++num;
			}
		}

		return num;
	}
}
