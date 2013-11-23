package tradewar.utils;

public class NotImplementedError extends AssertionError {

	private static final long serialVersionUID = -2120095794334697714L;

	
	public NotImplementedError() {
		
	}
	

	public NotImplementedError(Throwable cause) {
		super(cause);
	}
	
	public NotImplementedError(String what) {
		super(what);
	}
	
	public NotImplementedError(String what, Throwable cause) {
		super(what, cause);
	}
}
