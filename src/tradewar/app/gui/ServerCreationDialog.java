package tradewar.app.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;
import tradewar.api.IConfig;
import tradewar.api.IModInfo;
import tradewar.api.IServerStartParams;
import tradewar.app.FormatPatterns;
import tradewar.utils.ConfigBinding;
import tradewar.utils.ConfigDocumentBinder;
import tradewar.utils.FormChecker;
import tradewar.utils.IValidityChangeListener;
import tradewar.utils.ValidityBackgroundChanger;

public class ServerCreationDialog extends JDialog {
	private static final long serialVersionUID = 2427292471730316929L;


	private static class IModInfoRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = -293882292359629135L;

		public Component getListCellRendererComponent( JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
	        Object item = value;

	        if( item instanceof IModInfo ) {
	            item = ( ( IModInfo ) item ).getName();
	        }
	        return super.getListCellRendererComponent( list, item, index, isSelected, cellHasFocus);
	    }
	}

	private boolean successfulClosed = false;
	
	private JTextField serverNameInput;
	private JSpinner gameServerPortInput;
	private JPasswordField serverPasswortInput;
	private JComboBox<IModInfo> modSelect;
	private ComboBoxModel<IModInfo> modSelectModel;
	
	private final Action createServerAction = new CreateServerAction();
	private final Action cancelAction = new CancelServerCreationAction();
	private JSpinner queryServerPortInput;
	private JSpinner maxPlayerInput;

	private int standardGameServerPort;
	private int standardQueryServerPort;
	
	private ConfigBinding cfgBinding = new ConfigBinding();
	private FormChecker formChecker = new FormChecker();

	private IConfig config;
	
	private IModInfo[] modList;


	/**
	 * Create the dialog.
	 */
	public ServerCreationDialog(IConfig config, IModInfo[] mods, int standardGameServerPort, int standardQueryServerPort) {

		this.config = config;
		this.standardGameServerPort = standardGameServerPort;
		this.standardQueryServerPort = standardQueryServerPort;
		this.modList = mods;
		
		setup();
		
		formChecker.addValidityChangeListener(new IValidityChangeListener() {
			
			@Override
			public void onValidityChange(boolean valid) {
				createServerAction.setEnabled(valid);
			}
		});
		cfgBinding.load();
		formChecker.setNotifying(true);
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
        
        modSelectModel = new DefaultComboBoxModel<IModInfo>(modList);
        modSelect = new JComboBox<>();
        modSelect.setModel(modSelectModel);
        modSelect.setRenderer(new IModInfoRenderer());
        panelModGroup.add(modSelect, "cell 0 0,growx");
        
        JPanel panelServerGroup = new JPanel();
        getContentPane().add(panelServerGroup, "cell 0 1 2 1,grow");
        panelServerGroup.setBorder(new TitledBorder(null, "Server", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panelServerGroup.setLayout(new MigLayout("", "[125.00,grow][60.00,fill]", "[][][][][][][][][]"));
        
        JLabel lblServerName = new JLabel("Name:");
        panelServerGroup.add(lblServerName, "cell 0 0");
        
        serverNameInput = new JTextField();
        panelServerGroup.add(serverNameInput, "cell 0 1 2 1,growx");
		cfgBinding.addBinder(new ConfigDocumentBinder(config, "server-name", serverNameInput.getDocument()));
        formChecker.addChecker(ValidityBackgroundChanger.createDocumentChecker(FormatPatterns.SERVERNAME, serverNameInput));
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
        
        
        pack();
        getRootPane().setDefaultButton(btnCreateButton);
        serverNameInput.requestFocusInWindow();
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
	
	public IModInfo getSelectedMod() {
		return modSelectModel.getElementAt(modSelect.getSelectedIndex());
	}
	
	private class CreateServerAction extends AbstractAction {
		private static final long serialVersionUID = -481275235997405043L;
		
		public CreateServerAction() {
			putValue(NAME, "Create");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			cfgBinding.save();
			successfulClosed = true;
			dispose();
		}
	}
	
	
	private class CancelServerCreationAction extends AbstractAction {
		private static final long serialVersionUID = 7435773577905605217L;
		
		public CancelServerCreationAction() {
			putValue(NAME, "Cancel");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	}
}
