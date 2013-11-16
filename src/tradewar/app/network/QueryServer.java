package tradewar.app.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import tradewar.api.IQueryServer;
import tradewar.api.IServerStartParams;
import tradewar.app.Application;
import tradewar.utils.log.Log;

public class QueryServer implements IQueryServer, Runnable {

	public static final String REQUEST_PHRASE = "tradewar-query-request";
	public static final String ANSWER_PREFIX = "tradewar-query-answer";
	public static final String ANSWER_SEPERATOR = "\n";
	
	private Log log = new Log(Application.LOGSTREAM, "query-server");
    private DatagramSocket serverSocket;
	private IServerStartParams ssparams;
	private boolean active = false;

    private byte[] serverInfoData;
	
	public QueryServer(IServerStartParams ssparams) {
		this.ssparams = ssparams;
		
		try {
			serverSocket = new DatagramSocket(ssparams.getQueryServerPort());
		} catch (SocketException e) {
			log.crit("Failed to create the query server!");
			log.excp(e);
			
			return;
		}
		
		String serverInfo =	ANSWER_PREFIX + ANSWER_SEPERATOR
						  +	ssparams.getServerName() + ANSWER_SEPERATOR
						  + (ssparams.getServerPassword().isEmpty() ? "no" : "yes") + ANSWER_SEPERATOR
						  + ssparams.getMaxPlayer() + ANSWER_SEPERATOR
						  + serverSocket.getInetAddress().getHostAddress() + ANSWER_SEPERATOR
						  + ssparams.getGameServerPort() + ANSWER_SEPERATOR;
		
		serverInfoData = serverInfo.getBytes();				  
	}


	@Override
	public void run() {

        byte[] receiveData = new byte[1024];
	
		while(active) {
			try {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				
		        serverSocket.receive(receivePacket);
		        String sentence = new String(receivePacket.getData());

		        InetAddress ipAddress = receivePacket.getAddress();
		        
		        if(sentence.equals(REQUEST_PHRASE)) {

			        int port = receivePacket.getPort();
			        DatagramPacket sendPacket = new DatagramPacket(serverInfoData, serverInfoData.length, ipAddress, port);
			        serverSocket.send(sendPacket);
		        }
		        
		        log.err("Bad request from " + ipAddress.getHostAddress() + "!");
		        
		        
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

	
	private void start() {
		if(!active) {
			active = true;
			new Thread(this).start();
		}
	}
}
