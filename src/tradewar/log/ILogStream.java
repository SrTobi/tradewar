package tradewar.log;



public interface ILogStream {
	
	public static final ILogStream sys = new SystemLogStream();
	
	void write(LogMessage msg);
	
	public class SystemLogStream implements ILogStream {

		@Override
		public void write(LogMessage msg) {
			System.out.format("[%tT][%15s]%s: %s%n", msg.getDate(), msg.getComponent(), msg.getPriority().getName(), msg.getMsg());
		}
		
	}
	
	public class PriorityLog implements ILogStream {

		private LogPriority priority;
		private ILogStream stream;
		
		public PriorityLog(ILogStream stream, LogPriority priority) {
			this.priority = priority;
			this.stream = stream;
		}
		
		@Override
		public void write(LogMessage msg) {
			if (msg.getPriority().isMoreImportantThen(priority)) {
				stream.write(msg);
			}
		}
		
	}
}