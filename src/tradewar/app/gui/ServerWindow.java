package tradewar.app.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class ServerWindow extends JFrame {

	private static final long serialVersionUID = 7718403869196073788L;

	
	JTabbedPane mainTabPane;
	
	WindowListener closeListener = new WindowAdapter() {
		@Override
		public void windowClosed(WindowEvent e) {
		}
		
		@Override
		public void windowClosing(WindowEvent e) {
		}
	};
	
	/**
	 * Create the frame.
	 */
	public ServerWindow() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		mainTabPane = new JTabbedPane();
		mainTabPane.setTabPlacement(JTabbedPane.BOTTOM);
		setContentPane(mainTabPane);
		
		addWindowListener(closeListener);
	}

	
	public void addServerFrame(ServerFrame serverFrame) {
		mainTabPane.add(serverFrame.getView(), serverFrame.getTitle());
	}
	
}
