package tradewar.app.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import tradewar.utils.log.Log;
import net.miginfocom.swing.MigLayout;

public class ExceptionDialog extends JDialog {
	
	private static final long serialVersionUID = 6707133560891881692L;
	
	private Action sendAction = new SendAction();
	private Action continueAction = new ContinueAction();

	/**
	 * Create the dialog.
	 */
	public ExceptionDialog(String title, String msg, Throwable e, boolean hardcore) {

		setBounds(100, 100, 550, 300);
		setMinimumSize(new Dimension(400, 250));
		setLocationRelativeTo(null);
		
		setTitle(title);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		setModalityType(ModalityType.APPLICATION_MODAL);
		getContentPane().setLayout(new MigLayout("", "[][grow][]", "[][4][grow][]"));

		final Icon icon = UIManager.getIcon("OptionPane.errorIcon");
		
		JPanel panel = new JPanel() {
			private static final long serialVersionUID = -1716061904414871064L;

			@Override
		    protected void paintComponent(Graphics g) {
		        super.paintComponent(g);
		        icon.paintIcon(this, g, 0, 0);      
		    }
		};
		
		Dimension iconDim = new Dimension(icon.getIconWidth(), icon.getIconHeight());
		panel.setPreferredSize(iconDim);
		panel.setMinimumSize(iconDim);
		getContentPane().add(panel, "cell 0 0");
		
		JLabel lblInfoLabel = new JLabel(msg);
		getContentPane().add(lblInfoLabel, "cell 1 0");
		
		JPanel stacktraceGroupPanel = new JPanel();
		stacktraceGroupPanel.setBorder(new TitledBorder(null, "Stacktrace", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		getContentPane().add(stacktraceGroupPanel, "cell 0 2 3 1,grow");
		stacktraceGroupPanel.setLayout(new BorderLayout(0, 0));

		String stackTrace = "unknwon!";
		
		if(e != null) {
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			stackTrace = writer.toString();
		}
		
		JTextPane txtStackTrace = new JTextPane();
		txtStackTrace.setEditable(false);
		txtStackTrace.setForeground(Color.RED);
		txtStackTrace.setBackground(new Color(255, 255, 255));
		txtStackTrace.setText(stackTrace);
		stacktraceGroupPanel.add(new JScrollPane(txtStackTrace), BorderLayout.CENTER);
		
		if(hardcore) {
			JButton btnSend = new JButton("Send");
			btnSend.setAction(sendAction);
			getContentPane().add(btnSend, "cell 1 3,alignx right");
		}

		JButton btnContinue = new JButton("Continue");
		btnContinue.setAction(continueAction);
		getContentPane().add(btnContinue, "cell 2 3");

		pack();
		btnContinue.requestFocusInWindow();
		getRootPane().setDefaultButton(btnContinue);
	}
	
	public static void normalFail(String title, String err_msg, Throwable e, Log log) {
		ExceptionDialog dlg = new ExceptionDialog(title, err_msg, e, false);
		if(log != null) {
			log.err(err_msg);
			log.excp(e);
		}
		dlg.setVisible(true);
	}
	
	public static void normalFail(String msg, Throwable e, Log log) {
		normalFail("Error!", msg, e, log);
	}
	
	public static void critFail(String title, String err_msg, Throwable e, Log log) {
		ExceptionDialog dlg = new ExceptionDialog(title, err_msg, e, true);
		if(log != null) {
			log.crit(err_msg);
			log.excp(e);
		}
		dlg.setVisible(true);
	}
	
	public static void critFail(String msg, Throwable e, Log log) {
		critFail("Error!", msg, e, log);
	}
	
	
	private class SendAction extends AbstractAction {

		private static final long serialVersionUID = 6103008783537561989L;

		public SendAction() {
			putValue(NAME, "Send");
			putValue(SHORT_DESCRIPTION, "Send a report to the maintainer!");
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// send report
		}
	}

	private class ContinueAction extends AbstractAction {

		private static final long serialVersionUID = -8757769261886428276L;

		public ContinueAction() {
			putValue(NAME, "Continue");
			putValue(SHORT_DESCRIPTION, "Simply close the exception dialog!");
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			ExceptionDialog.this.dispose();
		}
	}
}
