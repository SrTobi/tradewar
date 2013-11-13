package tradewar.app.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import sun.awt.ModalityListener;
import tradewar.api.IModInfo;
import tradewar.api.IServerStartParams;
import net.miginfocom.swing.MigLayout;

public class ServerCreationDialog extends JDialog {


	private boolean successfulClosed = false;
	
	private JTextField serverNameInput;
	private JSpinner gameServerPortInput;
	private JPasswordField serverPasswortInput;
	private JComboBox<IModInfo> modSelect;
	
	private final Action createServerAction = new CreateServerAction();
	private final Action cancelAction = new CancelServerCreationAction();
	private JSpinner queryServerPortInput;
	private JSpinner maxPlayerInput;

	private int standardGameServerPort;
	private int standardQueryServerPort;
	
	private IModInfo[] modList;

	/**
	 * Create the dialog.
	 */
	public ServerCreationDialog(IModInfo[] mods, int standardGameServerPort, int standardQueryServerPort) {

		this.standardGameServerPort = standardGameServerPort;
		this.standardQueryServerPort = standardQueryServerPort;
		this.modList = mods;
		
		setup();
	}

	private void setup() {

        setTitle("Create server");
		setBounds(100, 100, 250, 350);
		setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
        setResizable(false);
        getContentPane().setLayout(new MigLayout("", "[grow][fill]", "[][grow][23px]"));
        
        JPanel panelModGroup = new JPanel();
        panelModGroup.setBorder(new TitledBorder(null, "Mod", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        getContentPane().add(panelModGroup, "cell 0 0 2 1,grow");
        panelModGroup.setLayout(new MigLayout("", "[grow]", "[]"));
        
        modSelect = new JComboBox<>();
        modSelect.setModel(new DefaultComboBoxModel<IModInfo>(modList));
        panelModGroup.add(modSelect, "cell 0 0,growx");
        
        JPanel panelServerGroup = new JPanel();
        getContentPane().add(panelServerGroup, "cell 0 1 2 1,grow");
        panelServerGroup.setBorder(new TitledBorder(null, "Server", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panelServerGroup.setLayout(new MigLayout("", "[125.00,grow][60.00,fill]", "[][][][][][][][][]"));
        
        JLabel lblServerName = new JLabel("Name:");
        panelServerGroup.add(lblServerName, "cell 0 0");
        
        serverNameInput = new JTextField();
        panelServerGroup.add(serverNameInput, "cell 0 1 2 1,growx");
        serverNameInput.setColumns(10);
        
        JLabel lblPassword = new JLabel("Password:");
        panelServerGroup.add(lblPassword, "cell 0 2");
        
        serverPasswortInput = new JPasswordField();
        panelServerGroup.add(serverPasswortInput, "cell 0 3 2 1,growx");
        
        JLabel lblPort = new JLabel("Port:");
        panelServerGroup.add(lblPort, "flowx,cell 0 4");
        
        gameServerPortInput = new JSpinner();
        gameServerPortInput.setModel(new SpinnerNumberModel(standardGameServerPort, 100, 65535, 1));
        panelServerGroup.add(gameServerPortInput, "cell 1 4,growx");
        
        JLabel lblQueryport = new JLabel("Query-Port:");
        panelServerGroup.add(lblQueryport, "cell 0 5");
        
        queryServerPortInput = new JSpinner();
        queryServerPortInput.setModel(new SpinnerNumberModel(standardQueryServerPort, 100, 65535, 1));
        panelServerGroup.add(queryServerPortInput, "cell 1 5");
        
        JLabel lblMaxPlayer = new JLabel("Max. Player:");
        panelServerGroup.add(lblMaxPlayer, "cell 0 6");
        
        maxPlayerInput = new JSpinner();
        maxPlayerInput.setModel(new SpinnerNumberModel(16, 0, null, 1));
        panelServerGroup.add(maxPlayerInput, "cell 1 6");
        
        JButton btnCreateButton = new JButton("Create");
        btnCreateButton.setAction(createServerAction);
        btnCreateButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        getContentPane().add(btnCreateButton, "flowx,cell 0 2,alignx right,aligny center");
        
        JButton btnCancelButton = new JButton("Cancel");
        btnCancelButton.setAction(cancelAction);
        getContentPane().add(btnCancelButton, "cell 1 2");
        
	}

	public boolean showDialog() {
		setVisible(true);
		
		return isSuccesfullyClosed();
	}
	
	public IServerStartParams getStartParams() {
		return new ServerStartParams(this);
	}
	
	public boolean isSuccesfullyClosed() {
		return successfulClosed;
	}
	
	public String getServerName() {
		return serverNameInput.getText();
	}
	
	public String getServerPassword() {
		return new String(serverPasswortInput.getPassword());
	}
	
	public int getGameServerPort() {
		return (Integer)gameServerPortInput.getValue();
	}
	
	public int getQueryServerPort() {
		return (Integer)queryServerPortInput.getValue();
	}
	
	public int getMaxPlayer() {
		return (Integer)maxPlayerInput.getValue();
	}
	
	private class CreateServerAction extends AbstractAction {
		public CreateServerAction() {
			putValue(NAME, "Create");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			
			successfulClosed = true;
			dispose();
		}
	}
	
	
	private class CancelServerCreationAction extends AbstractAction {
		public CancelServerCreationAction() {
			putValue(NAME, "Cancel");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	}
}
