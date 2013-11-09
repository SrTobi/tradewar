package tradewar.app.gui;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;

import tradewar.api.IScene;
import tradewar.api.ISceneFrame;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Test2Scene extends JPanel implements IScene{

	
	private ISceneFrame frame;
	
	/**
	 * Create the panel.
	 */
	public Test2Scene(ISceneFrame _frame) {
		
		this.frame = _frame;
		
		setLayout(new MigLayout("", "[grow][]", "[grow][]"));
		
		JTree tree = new JTree();
		tree.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("Ein kleiner Tree") {
				{
					DefaultMutableTreeNode node_1;
					add(new DefaultMutableTreeNode("und wie gehts"));
					node_1 = new DefaultMutableTreeNode("und ein ordner");
						node_1.add(new DefaultMutableTreeNode("und noch ein subitem"));
					add(node_1);
				}
			}
		));
		add(tree, "cell 0 0 2 1,grow");
		
		JButton btnNewButton = new JButton("Cancel");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame.removeScene(Test2Scene.this);
			}
		});
		add(btnNewButton, "cell 1 1");

	}

	@Override
	public Component getView() {
		return this;
	}

	@Override
	public String getSceneName() {
		
		return "test-2-scene";
	}

	@Override
	public String getSceneTitle() {
		
		return "Test2 Scene";
	}

	@Override
	public void onRegister() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUnregister() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEnter() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLeave() {
		// TODO Auto-generated method stub
		
	}

}
