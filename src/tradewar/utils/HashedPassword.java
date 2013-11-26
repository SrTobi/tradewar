package tradewar.utils;

import java.io.Serializable;
import java.security.MessageDigest;

public class HashedPassword implements Serializable {

	private static final long serialVersionUID = 8064407991169805487L;
	
	private final byte[] hashedPassword;
	
	private HashedPassword(byte[] hashedPassword) {
		this.hashedPassword = hashedPassword;
	}

	
	public byte[] getHashedPassword() {
		return hashedPassword;
	}
	
	@Override
	public String toString() {
		return new String(getHashedPassword());
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
		} else if (other instanceof String) {
			hashedpw = ((String) other).getBytes();
		}

		return hashedpw != null && MessageDigest.isEqual(hashedPassword, hashedpw);
	}
	
	private static byte[] hash(String clearString) {
		return Hasher.hash(clearString);
	}
	
	public static HashedPassword fromClean(String cleanString) {
		return new HashedPassword(hash(cleanString));
	}

	public static HashedPassword fromHash(String hashedString) {
		if(!hashedString.matches(Hasher.HASH_PATTERN)) {
			throw new IllegalArgumentException("hashedString does not contain a hash!");
		}
		
		return new HashedPassword(hashedString.getBytes());
	}

	public static HashedPassword fromHash(byte[] hashedBytes) {
		if(!new String(hashedBytes).matches(Hasher.HASH_PATTERN)) {
			throw new IllegalArgumentException("hashedString does not contain a hash!");
		}
		
		return new HashedPassword(hashedBytes);
	}
}
