package tradewar.app.mods.classic.gui;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

import net.miginfocom.swing.MigLayout;
import tradewar.api.ILogStream;
import tradewar.api.IPacket;
import tradewar.api.IScene;
import tradewar.api.ISocket;
import tradewar.api.ISocketListener;
import tradewar.app.mods.classic.packets.SendChatMessagePacket;
import tradewar.app.network.IPacketHandler;
import tradewar.app.network.PacketDistributor;
import tradewar.utils.log.Log;

public class LobbyScene extends JPanel implements IScene {
	private static final long serialVersionUID = 6229548365758192106L;
	
	private Log log;
	
	private JTextField inputField;
	private JTextArea outputField;
	private String nickname;
	private ISocket connection;
	
	
	private Action sendChatMessageAction = new SendChatMessageAction();
	private PacketDistributor distributor;
	
	private ISocketListener socketListener = new ISocketListener() {
		
		@Override
		public void onSend(IPacket packet) {}
		
		@Override
		public void onReceive(IPacket packet) {
			try {
				distributor.distribute(packet);
			} catch (Exception e) {
				log.excp(e);
			}
		}
		
		@Override
		public void onError(IOException e) {
		}
		
		@Override
		public void onDisconnect() {
		}
	};
	
	private IPacketHandler<SendChatMessagePacket> messageHandler = new IPacketHandler<SendChatMessagePacket>() {
		
		@Override
		public void onPacket(SendChatMessagePacket packet) throws Exception {
			StringBuilder line = new StringBuilder();
			if(packet.msgByServer) {
				line.append(packet.sender);
				line.append(": ");
				line.append(packet.message);
			}else{
				line.append('[');
				line.append(packet.sender);
				line.append("]: ");
				line.append(packet.message);
			}
			line.append('\n');
			
			outputField.append(line.toString());
		}
		
		@Override
		public Class<SendChatMessagePacket> getPacketClass() {
			return SendChatMessagePacket.class;
		}
	};

	/**
	 * Create the panel.
	 */
	public LobbyScene(ILogStream stream, String nickname, ISocket connection) {
		log = new Log(stream);
		distributor = new PacketDistributor(stream);
		distributor.addPacketHandler(messageHandler);
		
		this.nickname = nickname;
		this.connection = connection;
		
		setup();
	}

	private void setup() {
		setLayout(new MigLayout("", "[][grow][]", "[][grow][][]"));
		
		JLabel lblLobby = new JLabel("Lobby");
		lblLobby.setFont(new Font("Tahoma", Font.PLAIN, 30));
		add(lblLobby, "cell 0 0 3 1,alignx center");
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, "cell 0 1 2 2,grow");
		
		outputField = new JTextArea();
		DefaultCaret caret = (DefaultCaret)outputField.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		outputField.setEditable(false);
		scrollPane.setViewportView(outputField);
		
		JButton btnDisconnectButton = new JButton("Disconnect");
		add(btnDisconnectButton, "cell 2 2,alignx center");
		
		JLabel lblNickname = new JLabel(nickname + ":");
		add(lblNickname, "cell 0 3,alignx trailing");
		
		inputField = new JTextField();
		inputField.setAction(sendChatMessageAction);
		add(inputField, "cell 1 3,growx");
		inputField.setColumns(10);
		
		JButton btnSendButton = new JButton();
		btnSendButton.setAction(sendChatMessageAction);
		btnSendButton.setText("Send");
		add(btnSendButton, "cell 2 3,growx");
	}
	
	private void sendMessageInput() {
		String msg = inputField.getText();
		
		if(!msg.isEmpty()) {
			inputField.setText("");
			IPacket packet = new SendChatMessagePacket(false, nickname, msg);
			
			connection.send(packet);
		}
	}

	@Override
	public Component getView() {
		return this;
	}

	@Override
	public String getSceneName() {

		return "tw-lobby";
	}

	@Override
	public String getSceneTitle() {
		return "TradeWar Lobby";
	}

	@Override
	public void onRegister() {
	}

	@Override
	public void onUnregister() {
	}

	@Override
	public void onEnter() {
		inputField.setText("");
		outputField.setText("");
		connection.addSocketListener(socketListener);
	}

	@Override
	public void onLeave() {
		connection.removeSocketListener(socketListener);
	}

	private class SendChatMessageAction extends AbstractAction {
		
		private static final long serialVersionUID = -970120298675350344L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			sendMessageInput();
		}
	}
}
