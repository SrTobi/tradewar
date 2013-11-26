package tradewar.app.network;

import java.text.ParseException;

import tradewar.api.IServerStartParams;
import tradewar.app.FormatPatterns;


public final class QueryResponse {

	public static final String REQUEST_PHRASE = "tradewar-query-request";
	public static final String ANSWER_PREFIX = "begin:tradewar-query-answer";
	public static final String ANSWER_SURFIX = "end:tradewar-query-answer";
	public static final String ANSWER_SEPERATOR = "\n";
	public static final String ANSWER_PATTERN_SERVERNAME = FormatPatterns.SERVERNAME;
	public static final String ANSWER_PATTERN_MODNAME = FormatPatterns.MODNAME;
	public static final String ANSWER_PATTERN_HASPASSWORD = "(yes)|(no)";
	public static final String ANSWER_PATTERN_SERVERADDR = FormatPatterns.SERVERADDR;
	public static final int ANSWER_LINES = 8;
	
	private final String serverName;
	private final String modification;
	private final boolean password;
	private final int playerCount;
	private final int maxPlayer;
	private final String serverAddress;
	private final int serverPort;
	
	
	public QueryResponse(String in) throws ParseException {
	
		String[] lines = in.split(ANSWER_SEPERATOR);
		
		if(lines.length != ANSWER_LINES) {
			throw new ParseException("Response has not the expected number of lines!", -1);
		}
		
		if(!lines[0].equals(ANSWER_PREFIX)) {
			throw new ParseException("Response prefix not existent [" + ANSWER_PREFIX + "]!", 0);
		}
		
		if(!lines[7].equals(ANSWER_SURFIX)) {
			throw new ParseException("Response surfix not present [" + ANSWER_SURFIX + "]!", 6);
		}

		serverName = checkLine(lines, 1, "Servername", ANSWER_PATTERN_SERVERNAME);
		modification = checkLine(lines, 2, "Modification", ANSWER_PATTERN_MODNAME);
		password = checkLine(lines, 3, "HasPassword", ANSWER_PATTERN_HASPASSWORD).equals("yes");
		serverAddress = checkLine(lines, 5, "Serveraddress", ANSWER_PATTERN_SERVERADDR);

		try {
			String[] nums = lines[4].split("/");
			playerCount = Integer.parseInt(nums[0]);
			maxPlayer = Integer.parseInt(nums[1]);
		}catch ( NumberFormatException e) {
			throw new ParseException("player-field is not a number!", 4);
		}
		
		try {
			serverPort = Integer.parseInt(lines[6]);
		} catch(NumberFormatException e) {
			throw new ParseException("ServerPort-field is not a number!", 6);
		}
		
		
		
	}
	
	private String checkLine(String[] lines, int linenum, String contentName, String pattern) throws ParseException {
		String line = lines[linenum];
		if(!line.matches(pattern)) {
			throw new ParseException(contentName + "-field does not match the allowed pattern[" + pattern + "]!", linenum);
		}
		
		return line;
	}
	
	public QueryResponse(IServerStartParams ssparams, String serverAddress, int playerCount) {
		this.serverName = ssparams.getServerName();
		this.modification = ssparams.getMod().getName();
		this.password = ssparams.getHashedServerPassword() != null;
		this.playerCount = playerCount;
		this.maxPlayer = ssparams.getMaxPlayer();
		this.serverAddress = serverAddress;
		this.serverPort = ssparams.getGameServerPort();
	}
	
	public String getServerName() {
		return serverName;
	}
	
	public String getModification() {
		return modification;
	}
	
	public boolean hasPassword() {
		return password;
	}

	private int getPlayerCount() {
		return playerCount;
	}
	
	public int getMaxPlayer() {
		return maxPlayer;
	}

	public String getServerAddress() {
		return serverAddress;
	}
	
	public int getServerPort() {
		return serverPort;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof QueryResponse) {
			QueryResponse other = (QueryResponse)obj;
			
			return other.getResponseData().equals(other.getResponseData());
		}
		
		return false;
	}
	
	public String getResponseData() {

		return	ANSWER_PREFIX + ANSWER_SEPERATOR
				+ getServerName() + ANSWER_SEPERATOR
				+ getModification() + ANSWER_SEPERATOR
				+ (hasPassword()? "yes" : "no") + ANSWER_SEPERATOR
				+ getPlayerCount() + "/" + getMaxPlayer() + ANSWER_SEPERATOR
				+ getServerAddress() + ANSWER_SEPERATOR
				+ getServerPort() + ANSWER_SEPERATOR
				+ ANSWER_SURFIX;
	}
}
