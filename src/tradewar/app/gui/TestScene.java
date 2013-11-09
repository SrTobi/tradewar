package tradewar.app.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import net.miginfocom.swing.MigLayout;
import tradewar.api.IScene;
import tradewar.api.ISceneFrame;


public class TestScene extends JPanel implements IScene {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8128963911405663492L;
	private final Action submitAction = new SwingAction();
	private JTextPane inputField;
	private ISceneFrame frame;
	
	/**
	 * Create the panel.
	 */
	public TestScene(ISceneFrame frame) {
		this.frame = frame;
		setLayout(new MigLayout("", "[grow][]", "[grow][]"));
		
		inputField = new JTextPane();
		add(inputField, "cell 0 0 2 1,grow");
		
		JButton btnNewButton = new JButton("Submit");
		btnNewButton.setAction(submitAction);
		add(btnNewButton, "cell 1 1");
	}

	private class SwingAction extends AbstractAction {

		private static final long serialVersionUID = 2705133872485747845L;
		
		public SwingAction() {
			putValue(NAME, "Submit");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(TestScene.this, "Text: " + inputField.getText());
			frame.setScene(new Test2Scene(frame));
		}
	}

	@Override
	public Component getView() {
		return this;
	}

	@Override
	public String getSceneName() {
		return "test-window";
	}

	@Override
	public String getSceneTitle() {

		return "Test Scene";
	}

	@Override
	public void onRegister() {}

	@Override
	public void onUnregister() {}

	@Override
	public void onEnter() {}

	@Override
	public void onLeave() {}
}
