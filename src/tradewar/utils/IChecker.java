package tradewar.utils;

public interface IChecker {
	public boolean isValid();
	public boolean check();
	
	public void setNotifying(boolean notifying);
	public boolean isNotifying();
	
	public void addValidityChangeListener(IValidityChangeListener listener);
	public void removeValidityChangeListener(IValidityChangeListener listener);
	
	public void registerFormChecker(FormChecker checker);
	public void unregisterFormChecker(FormChecker checker);
}
