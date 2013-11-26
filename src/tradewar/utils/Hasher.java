package tradewar.utils;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Hasher {
	
	public static final String HASH_PATTERN = "[0-9a-f]+";
	
	private static final Charset UTF8_CHARSET;
	private static final MessageDigest SHA256_DIGIST;
	
	static {
		try {
			UTF8_CHARSET = Charset.forName("UTF-8"); 
	    } catch (UnsupportedCharsetException | IllegalCharsetNameException e) {
			throw new NotImplementedError("No UTF-8 support! [" + e.getMessage() + "]", e);
	    }
    
		try {
			SHA256_DIGIST = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new NotImplementedError("No SHA-256 support!", e);
		}
	}

	public static boolean isEqual(byte[] a, byte[] b) {
		return MessageDigest.isEqual(a, b);
	}

	public static byte[] hash(String seq) {
		byte[] bytes = seq.getBytes(UTF8_CHARSET);

		byte[] code = SHA256_DIGIST.digest(bytes);
		
		return code;
	}
	
	public static String hashString(String seq) {
		return hexify(hash(seq));
	}
	
	public static String hexify(byte[] seq) {
		StringBuffer hexString = new StringBuffer();

        for (int i = 0; i < seq.length; i++) {
            String hex = Integer.toHexString(0xff & seq[i]);
            hexString.append(hex);
        }
        
        return hexString.toString();
	}
}
