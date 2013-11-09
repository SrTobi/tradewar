package tradewar.log;

import java.util.Date;

public class LogMessage {
	
	String component;
	String msg;
	Date date;
	LogPriority priority;
	
	
	LogMessage(String msg, String component, LogPriority priority) {
		date = new Date();
		this.priority = priority;
		this.msg = msg;
		this.component = component;
	}

	public String getComponent() {
		return component;
	}

	public String getMsg() {
		return msg;
	}

	public Date getDate() {
		return date;
	}

	public LogPriority getPriority() {
		return priority;
	}
}