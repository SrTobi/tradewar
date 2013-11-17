package tradewar.app.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import tradewar.api.IQueryServer;
import tradewar.api.IServer;
import tradewar.api.IServerStartParams;
import tradewar.app.Application;
import tradewar.utils.log.Log;

public class QueryServer implements IQueryServer, Runnable {

	
	private Log log = new Log(Application.LOGSTREAM, "query-server");
    private DatagramSocket serverSocket;
	private boolean active = false;
	
	private IServerStartParams ssparams;
	private String serverAddr;
	private IServer server;
	
	public QueryServer(IServerStartParams ssparams) {
		
		this.server = null;
		this.ssparams = ssparams;
		
		try {
			serverSocket = new DatagramSocket(ssparams.getQueryServerPort());
		} catch (SocketException e) {
			log.crit("Failed to create the query server!");
			log.excp(e);
			
			return;
		}
		
		try {
			setServerAddress(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			log.err("Failed to resolve local ip-address!");
			log.excp(e);
		}
	}

	public void setServer(IServer server) {
		this.server = server;
	}
	
	@Override
	public void setServerAddress(String addr) {

		if(addr.matches(QueryResponse.ANSWER_PATTERN_SERVERADDR)) {
			serverAddr = addr;
		}
	}
	
	
	@Override
	public void run() {

        byte[] receiveData = new byte[1024];
	
		while(active) {
			try {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				
		        serverSocket.receive(receivePacket);
		        String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());

		        InetAddress ipAddress = receivePacket.getAddress();
		        
		        if(sentence.equals(QueryResponse.REQUEST_PHRASE)) {

		        	if(serverAddr != null && server != null) {
			        	byte[] response = createResponse().getResponseData().getBytes();
				        int port = receivePacket.getPort();
				        DatagramPacket sendPacket = new DatagramPacket(response, response.length, ipAddress, port);
				        serverSocket.send(sendPacket);
		        	}
		        } else {
			        log.err("Bad request from " + ipAddress.getHostAddress() + "!");
		        }
		        
			}catch(IOException e) {
				log.err("Error while waiting for request! Never mind...");
				log.excp(e);
			}
		}
	}
	
	@Override
	public void setActive(boolean active) {

		if(serverSocket == null)
			return;
		
		if(active && !this.active) {
			start();
		} else {
			this.active = false;
		}
	}

	@Override
	public boolean isActive() {
		return active;
	}

	
	private QueryResponse createResponse() {

		QueryResponse response = new QueryResponse(	ssparams.getServerName(),
													null,
													!ssparams.getServerPassword().isEmpty(),
													0,
													server.getPlayerCount(),
													ssparams.getMaxPlayer(),
													serverAddr,
													ssparams.getGameServerPort());
		
		return response;
	}
	
	private void start() {
		if(!active) {
			active = true;
			new Thread(this).start();
		}
	}
}
