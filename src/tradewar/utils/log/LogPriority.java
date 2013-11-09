package tradewar.utils.log;


public enum LogPriority {
	WTF(0, "WTF"),
	CRIT(0, "CRIT"),
	ERROR(1, "ERR "),
	WARN(2, "WARN"), 
	INFO(3, "INFO"),
	DEBUG(4, "DBUG");

	private int priority;
	private String name;

	private LogPriority(int priority, String name) {
		this.priority = priority;
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public boolean isMoreImportantThen(LogPriority prio) {
		return prio.priority >= this.priority;
	}
}	