package tradewar.utils;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashedPassword implements Serializable {

	private static final long serialVersionUID = 8064407991169805487L;
	
	
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
	
	private final byte[] hashedPassword;
	
	public HashedPassword(String clearPassword) {
		hashedPassword = hash(clearPassword);
	}

	
	public byte[] getHashedPassword() {
		return hashedPassword;
	}
	
	@Override
	public boolean equals(Object other) {

		if(other == null) {
			return false;
		}
		
		if(other == this) {
			return true;
		}

		byte[] hashedpw = null;
		
		if(other instanceof HashedPassword) {
			hashedpw = ((HashedPassword) other).getHashedPassword();
		} else if (other instanceof byte[]) {
			hashedpw = (byte[]) other;
		}

		return hashedpw != null && MessageDigest.isEqual(hashedPassword, hashedpw);
	}
	
	private byte[] hash(String clearString) {
		byte[] bytes = clearString.getBytes(UTF8_CHARSET);

		return SHA256_DIGIST.digest(bytes);
	}
}
