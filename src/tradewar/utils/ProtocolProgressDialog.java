package tradewar.utils;

import tradewar.app.network.IProtocolListener;

public class ProtocolProgressDialog extends ProcessDialog {

	private static final long serialVersionUID = 6737820543657906056L;

	private AbstractProtocol protocol;
	private IProtocolListener protocolListener = new IProtocolListener() {
		
		@Override
		public void onProtocolFail(Exception failure) {
			// stop
			ProtocolProgressDialog.this.dispose();
		}
		
		@Override
		public void onProtocolCompleteness() {
			// stop
			ProtocolProgressDialog.this.dispose();
		}
		
		@Override
		public void onProtocolAbort() {
			// do nothing!
		}
	};
	
	public ProtocolProgressDialog(String status, AbstractProtocol protocol) {
		super(status);
		
		this.protocol = protocol;
		
		protocol.addProtocolListener(protocolListener);
	}
	
	public AbstractProtocol getProtocol() {
		return protocol;
	}
	
	public AbstractProtocol.ProtocolState showDlg() {
		protocol.invokeProtocol();
		setVisible(true);
		
		return protocol.getState();
	}
	
	@Override
	protected void onCancel() {
		protocol.abort();
	}
	
}
