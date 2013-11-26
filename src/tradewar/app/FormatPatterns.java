package tradewar.app;

import tradewar.utils.Hasher;

public final class FormatPatterns {
	public static final String SERVERNAME = "[\\p{Blank}\\p{Graph}]{3,20}";
	public static final String MODNAME = "[\\p{Blank}\\p{Graph}]{3,20}";
	public static final String SERVERADDR = "[\\p{Blank}\\p{Graph}]{1,150}";
	
	public static final String NICKNAME = "\\p{Graph}{2,10}";
	
	public static final String MOD_UID = Hasher.HASH_PATTERN;
}
