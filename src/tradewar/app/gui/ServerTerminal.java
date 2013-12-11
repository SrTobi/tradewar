package tradewar.app.gui;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import tradewar.api.IScene;
import tradewar.api.IServer;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JScrollPane;

public class ServerTerminal extends JPanel implements IScene{

	private static final long serialVersionUID = 6005334939337146985L;

	IServer server;
	private JTextArea terminalOutput;
	private JTextField terminalInput;
	
	public ServerTerminal(IServer server) {
		this.server = server;
		
		setup();
	}
	
	private void setup() {
		setBorder(new EmptyBorder(8, 8, 8, 8));
		setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(new EmptyBorder(0, 0, 4, 0));
		
		terminalOutput = new JTextArea();
		terminalOutput.setBackground(Color.BLACK);
		terminalOutput.setForeground(new Color(0, 255, 0));
		terminalOutput.setEditable(false);

		DefaultCaret caret = (DefaultCaret)terminalOutput.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		scrollPane.setViewportView(terminalOutput);
		add(scrollPane, BorderLayout.CENTER);
		
		
		terminalInput = new JTextField();
		add(terminalInput, BorderLayout.SOUTH);
		terminalInput.setColumns(10);
		
		terminalInput.addActionListener(new ActionListener(){

	            public void actionPerformed(ActionEvent e){
	            	
	            	String cmd = terminalInput.getText();
	            	terminalInput.setText("");
	            	
	            	if(cmd != null && !cmd.isEmpty()) {
	            		server.onTextCommand(cmd);
	            	}
	            }
            });
	}
	
	public PrintStream getPrintStream() {
		return new PrintStream(new OutputStream() {
			
			@Override
			public void write(int b) throws IOException {
				char c = (char)b;
				
				terminalOutput.append(Character.toString(c));
			}
		});
	}

	@Override
	public Component getView() {
		return this;
	}

	@Override
	public String getSceneName() {
		return "server-terminal";
	}

	@Override
	public String getSceneTitle() {
		return "Terminal";
	}

	@Override
	public void onRegister() {
	}

	@Override
	public void onUnregister() {
	}

	@Override
	public void onEnter() {
	}

	@Override
	public void onLeave() {
	}

}
