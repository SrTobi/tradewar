package tradewar.app.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;
import tradewar.api.ILogStream;
import tradewar.utils.log.Log;

public class QueryEmitter implements Runnable {
	
	private Log log;
	
	private Collection<IQueryResponseListener> listener = new HashSet<>();
	private DatagramSocket socket;
	private boolean searching = false;
	private int targetPort;
		
	public QueryEmitter(ILogStream logstream, int port) throws SocketException {
		log = new Log(logstream, "query-emitter");
		
		this.targetPort = port;
		socket = new DatagramSocket();
	}
	
	public void addResponseListener(IQueryResponseListener listener) {
		this.listener.add(listener);
	}
	
	public void removeResponseListener(IQueryResponseListener listener) {
		this.listener.remove(listener);
	}
	
	public void removeAllResponseListener() {
		this.listener.clear();
	}
	
	public void search(boolean search) {
		if(search && !isSearching()) {
			searching = true;
			new Thread(this).start();
		} else {
			searching = false;
		}
	}
	
	public boolean isSearching() {
		return searching;
	}

	@Override
	public void run() {

		searching = broadcastRequest();

		byte[] receiveData = new byte[1024];
		while(isSearching()) {
			
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
				socket.receive(receivePacket);
			} catch (IOException e) {
				log.err("Failed to receive a response!");
				log.excp(e);
			}
            
            try {
            	QueryResponse response = new QueryResponse(new String(receivePacket.getData(), 0, receivePacket.getLength()));
            	notifyListener(response);
            } catch(ParseException e) {
            	log.err("Received wrong request!");
            	log.excp(e);
            }
		}
	}
	
	private boolean broadcastRequest() {
		
		InetAddress addr;
		try {
			addr = InetAddress.getByName("192.168.1.255");
		} catch (UnknownHostException e) {
			log.err("Failed to resolve broadcast ip-address!");
			log.excp(e);
			
			return false;
		}
		//log.info("Own ip is " + addr.getHostAddress());
		log.debug("Broadcasting query request[" + addr.getHostAddress() + "]...");
		
		byte[] request = QueryResponse.REQUEST_PHRASE.getBytes();
		DatagramPacket packet = new DatagramPacket(request, request.length, addr, targetPort);
		
		try {
			socket.send(packet);
		} catch (IOException e) {
			log.err("Failed to broadcast query request!");
			log.excp(e);
			
			return false;
		}
		
		return true;
	}
	
	private void notifyListener(QueryResponse response) {
		for(IQueryResponseListener l : listener) {
			l.onResponse(response);
		}
	}
}
