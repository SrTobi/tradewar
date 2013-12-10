package tradewar.utils;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import net.miginfocom.swing.MigLayout;

public class ProcessDialog extends JDialog {

	private static final long serialVersionUID = 966379407800745444L;

	
	private Action cancelAction = new CancelAction();
	private JLabel lblStatusLabel;
	private JProgressBar progressBar;
	
	public ProcessDialog() {
		setup();
	}
	
	public ProcessDialog(String status) {
		this();
		
		setStatus(status);
	}
	

	public void setup() {
		setTitle("Connecting...");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);

		setBounds(100, 100, 400, 120);
		setMinimumSize(new Dimension(400, 120));
		getContentPane().setLayout(new MigLayout("", "[66.00,grow][]", "[grow][25px:n][grow]"));
		
		lblStatusLabel = new JLabel();
		getContentPane().add(lblStatusLabel, "cell 0 0 2 1,growx,aligny center");
		
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setMinimum(0);
		progressBar.setMaximum(1000);
		
		getContentPane().add(progressBar, "cell 0 1 2 1,grow");
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setAction(cancelAction);
		getContentPane().add(btnCancel, "cell 1 2,alignx right,aligny bottom");

		setResizable(false);
		setLocationRelativeTo(null);
		
		pack();
	}
	
	public void setStatus(String status) {
		lblStatusLabel.setText(status);
	}
	
	public void setIndetreminate(boolean indeterminate) {
		progressBar.setIndeterminate(indeterminate);
	}
	
	public boolean isIndeterminate() {
		return progressBar.isIndeterminate();
	}
	
	public void setProgress(float progress) {
		if(progress < 0.0f || progress > 1.0f) {
			throw new IllegalArgumentException("progress can only be between 0 and 1");
		}
	
		progressBar.setValue((int)(1000 * progress));
	}
	
	
	protected void onCancel() {
		
	}

	class CancelAction extends AbstractAction {

		private static final long serialVersionUID = 8837039819400458762L;


		public CancelAction() {
			putValue(NAME, "Cancel");
			putValue(SHORT_DESCRIPTION, "Cancels connection attempt");
		}
		
		
		@Override
		public void actionPerformed(ActionEvent evt) {
			onCancel();
			ProcessDialog.this.dispose();
		}
		
	}
}
