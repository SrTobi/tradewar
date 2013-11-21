package tradewar.utils;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractChecker implements IChecker {

	private List<FormChecker> formCheckers = new ArrayList<>();
	private List<IValidityChangeListener> listeners = new ArrayList<>();
	private boolean valid = false;
	private boolean notifying = true;
	
	/**
	 * Dont override! Override checkIfValid
	 */
	@Override
	public boolean isValid() {
		return valid;
	}

	@Override
	public boolean check() {
		notifyPossibleValidityChange();
		
		return valid;
	}
	
	@Override
	public void setNotifying(boolean notifying) {
		
		if(notifying && !this.notifying) {
			check();
			
			this.notifying = true;
			
			notifyListeners(valid);
		} else {
			this.notifying = notifying;
		}
	}

	@Override
	public boolean isNotifying() {
		return notifying;
	}

	public void addValidityChangeListener(IValidityChangeListener listener) {
		listeners.add(listener);
		if(isNotifying())
			listener.onValidityChange(valid);
	}
	
	public void removeValidityChangeListener(IValidityChangeListener listener) {
		listeners.remove(listener);
	}
	
	@Override
	public void registerFormChecker(FormChecker checker) {
		formCheckers.add(checker);
	}

	@Override
	public void unregisterFormChecker(FormChecker checker) {
		formCheckers.add(checker);
	}
	
	
	protected void notifyPossibleValidityChange() {

		if(checkIfValid() != valid) {

			valid = !valid;
			
			if(notifying)
				notifyListeners(valid);
			
			for(FormChecker fchecker : formCheckers) {
				fchecker.notifyPossibleValidityChange();
			}
		}
	}
	
	protected abstract boolean checkIfValid();
	
	private void notifyListeners(boolean valid) {
		for(IValidityChangeListener l : listeners) {
			l.onValidityChange(valid);
		}
	}
}
