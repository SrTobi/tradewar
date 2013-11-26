package tradewar.app;

import tradewar.utils.log.Log;

public class AppException extends Exception {
	
	private static final long serialVersionUID = -1374533725180907573L;

	protected Log log = new Log(Application.LOGSTREAM, "app");
	
	
	public AppException(String info) {
		super(info);
		log.crit(info);
	}
	
	public AppException(String info, Throwable cause) {
		super(info, cause);
		log.crit(info);
	}	
}
