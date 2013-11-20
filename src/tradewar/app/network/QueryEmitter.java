package tradewar.app.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Enumeration;
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

		System.setProperty("java.net.preferIPv4Stack", "true");
		
		Enumeration<NetworkInterface> niEnum = null;
		try {
			niEnum = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			log.crit("Failed to get network interfaces for broadcast!");
			log.excp(e);
			return false;
		}

		// build packet
		byte[] request = QueryResponse.REQUEST_PHRASE.getBytes();		
		
		while (niEnum.hasMoreElements()) {
			NetworkInterface ni = niEnum.nextElement();
			log.debug("Found network interface:" + ni.getDisplayName());
			
			for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {
				InetAddress addr = interfaceAddress.getBroadcast();
				
				if(addr == null) {
					continue;
				}
				
				log.info("Broadcast query request to " + addr);

				DatagramPacket packet = new DatagramPacket(request, request.length, addr, targetPort);
				
				try {
					socket.send(packet);
				} catch (IOException e) {
					log.err("Failed to broadcast query request!");
					log.excp(e);
				}
			}
		}
		
		return true;
	}
	
	private void notifyListener(QueryResponse response) {
		for(IQueryResponseListener l : listener) {
			l.onResponse(response);
		}
	}
}
