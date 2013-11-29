package tradewar.utils;

import java.util.ArrayList;
import java.util.List;

import tradewar.app.Application;
import tradewar.app.network.IProtocolListener;
import tradewar.utils.log.Log;

public abstract class AbstractProtocol {

	public enum ProtocolState {
		Ready,
		Running,
		Completed,
		Failed,
		Aborted
	}
	
	protected Log log = new Log(Application.LOGSTREAM, "protocol");
	private List<IProtocolListener> listeners = new ArrayList<>();
	private ProtocolState state = ProtocolState.Ready;
	private Exception failure;
	
	public void addProtocolListener(IProtocolListener listener) {
		listeners.add(listener);
	}

	public void removeProtocolListener(IProtocolListener listener) {
		listeners.remove(listener);
	}

	public void executeProtocol() throws Exception {
		if(started()) {
			throw new IllegalStateException("Protocol already started!");
		}
		state = ProtocolState.Running;

		startProtocol();
	}

	public void invokeProtocol() {
		if(started()) {
			throw new IllegalStateException("Protocol already started!");
		}
		state = ProtocolState.Running;
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					startProtocol();
				} catch (Exception e) {
					log.err("Protocol failed: " + e.getMessage());
				}
			}
		}).start();
	}
	
	public void abort() {
		state = ProtocolState.Aborted;
		notifyProtocolAbort();
	}
	
	public boolean started() {
		return state != ProtocolState.Ready;
	}
	
	public boolean aborted() {
		return state == ProtocolState.Aborted;
	}
	
	public boolean completed() {
		return state == ProtocolState.Completed;
	}

	public boolean failed() {
		return state == ProtocolState.Failed;
	}
	
	public ProtocolState getState() {
		return state;
	}
	
	public Exception getFailure() {
		return failure;
	}
	
	public boolean isProgressIndeterminated() {
		return true;
	}
	
	public float getProgress() {
		throw new IllegalAccessError("Protocol has no progress!");
	}
	
	protected void notifyProtocolFail(Exception failure) {
		for(IProtocolListener listener : listeners) {
			listener.onProtocolFail(failure);
		}
	}

	protected void notifyProtocolCompleteness() {
		for(IProtocolListener listener : listeners) {
			listener.onProtocolCompleteness();
		}
	}
	

	protected void notifyProtocolAbort() {
		for(IProtocolListener listener : listeners) {
			listener.onProtocolAbort();
		}
	}
	
	private void startProtocol() throws Exception {
		
		try {
			handleProtocol();
			if(!aborted()) {
				state = ProtocolState.Completed;
				notifyProtocolCompleteness();
			}
		} catch(Exception e) {
			if(!aborted()) {
				failure = e;
				state = ProtocolState.Failed;
				notifyProtocolFail(e);
			}
			
			throw e;
		}
	}
	
	protected abstract void handleProtocol() throws Exception;
}
