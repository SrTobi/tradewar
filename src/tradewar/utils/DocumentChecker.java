package tradewar.utils;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class DocumentChecker extends AbstractChecker {

	private String pattern;
	private Document document;
	
	public DocumentChecker(String pattern, Document document) {
		this.pattern = pattern;
		this.document = document;
		
		document.addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				notifyPossibleValidityChange();
			}
			
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				notifyPossibleValidityChange();
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				notifyPossibleValidityChange();
			}
		});
	}

	@Override
	protected boolean checkIfValid() {
		try {
			return document.getText(0, document.getLength()).matches(pattern);
		} catch (BadLocationException e) {
			return false;
		}
	}

	/*#
	
	private Border wrongBorder = BorderFactory.createLineBorder(Color.RED);
	private Border validBorder;
	
		this.validBorder = getBorder();
	private List<FormChecker> checkers = new ArrayList<>();
	
	@Override
	public boolean isValid() {
		return getText() != null && getText().matches(pattern);
	}

	@Override
	public void addFormInputChecker(FormChecker checker) {
		checkers.add(checker);
	}

	@Override
	public void removeFormInputChecker(FormChecker checker) {
		checkers.remove(checker);
	}

	private void checkMyself() {

		setBorder(isValid());
		
	}
	
	private void setBorder(boolean valid) {
		setBorder(valid? validBorder : wrongBorder);
	}*/
}
