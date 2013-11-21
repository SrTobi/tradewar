package tradewar.utils;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JTextField;

public class ValidityBackgroundChanger implements IValidityChangeListener {

	private static final Color INVALID_COLOR = new Color(255, 210, 210);
	
	Color colorForValidState;
	Color colorForInvalidState;
	JComponent borderedComponent;

	public ValidityBackgroundChanger(JComponent component) {
		this(component, INVALID_COLOR);
	}
	
	public ValidityBackgroundChanger(JComponent component, Color colorForInvalidState) {

		this(component, component.getBackground(), colorForInvalidState);
	}
	
	public ValidityBackgroundChanger(JComponent component, Color colorForValidState, Color colorForInvalidState) {
		this.borderedComponent = component;
		this.colorForValidState = colorForValidState;
		this.colorForInvalidState = colorForInvalidState;
	}
	
	
	@Override
	public void onValidityChange(boolean valid) {
		borderedComponent.setBackground(valid? colorForValidState : colorForInvalidState);
	}

	
	
	public static IChecker createDocumentChecker(String pattern, JTextField txtField, Color colorForInvalidState) {
		IChecker checker = new DocumentChecker(pattern, txtField.getDocument());
		
		checker.addValidityChangeListener(new ValidityBackgroundChanger(txtField, colorForInvalidState));
		
		return checker;
	}
	
	public static IChecker createDocumentChecker(String pattern, JTextField txtField) {
		IChecker checker = new DocumentChecker(pattern, txtField.getDocument());
		
		checker.addValidityChangeListener(new ValidityBackgroundChanger(txtField));
		
		return checker;
	}
}
