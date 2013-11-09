package tradewar.utils.log;

public class Log {
	
	public static Log log = new Log(new ILogStream.SystemLogStream(), "global");
	
	ILogStream stream;
	String component;
	
	
	public Log(ILogStream stream) {
		this(stream, "unknown");
	}
	
	
	public Log(ILogStream stream, String component) {
		this.stream = stream;
		this.component = component;
	}
	
	public void wtf(String msg) {
		stream.write(new LogMessage(msg, component, LogPriority.WTF));
		throw new AssertionError(msg, null);
	}

	public void crit(String msg) {
		stream.write(new LogMessage(msg, component, LogPriority.CRIT));
	}

	public void err(String msg) {
		stream.write(new LogMessage(msg, component, LogPriority.ERROR));
	}

	public void warn(String msg) {
		stream.write(new LogMessage(msg, component, LogPriority.WARN));
	}
	
	public void info(String msg) {
		stream.write(new LogMessage(msg, component, LogPriority.INFO));
	}

	public void debug(String msg) {
		stream.write(new LogMessage(msg, component, LogPriority.DEBUG));
	}
	
	public ILogStream getStream() {
		return stream;
	}
}
