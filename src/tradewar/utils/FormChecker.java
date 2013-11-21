package tradewar.utils;

import java.util.ArrayList;
import java.util.List;

public class FormChecker extends AbstractChecker {
	
	private List<IChecker> checkers = new ArrayList<>();
	
	public FormChecker() {
		setNotifying(false);
	}
	
	public void addChecker(IChecker input) {
		checkers.add(input);
		input.registerFormChecker(this);
		notifyPossibleValidityChange();
	}
	
	public void removeChecker(IChecker input) {
		checkers.remove(input);
		input.unregisterFormChecker(this);
		notifyPossibleValidityChange();
	}
	
	@Override
	public boolean check() {
		for(IChecker checker : checkers) {
			checker.check();
		}
		
		return super.check();
	}

	@Override
	protected boolean checkIfValid() {
		boolean nowValid = true;
		
		
loop:	for(IChecker checker : checkers) {
			if(!checker.isValid()) {
				nowValid = false;
				break loop;
			}
		}
		
		return nowValid;
	}
}
