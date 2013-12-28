package tradewar.app.gui;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTabbedPane;

import tradewar.api.IScene;
import tradewar.api.ISceneFrame;
import tradewar.api.IServer;

public class ServerFrame implements ISceneFrame {

	private Map<IScene, Component> sceneToComponentMapping = new HashMap<>();
	private IServer server;
	private String title;
	
	JTabbedPane serverTabs;
	
	public ServerFrame(String title, IServer server) {
		this.title = title;
		this.server = server;
		
		serverTabs = new JTabbedPane();
		serverTabs.setTabPlacement(JTabbedPane.LEFT);
	}
	
	public String getTitle() {
		return title;
	}
	
	public Component getView() {
		return serverTabs;
	}
	
	@Override
	public void setScene(IScene scene) {
		
		if(!sceneToComponentMapping.containsKey(scene)) {
		
			Component c = scene.getView();
			sceneToComponentMapping.put(scene, c);
			
			scene.onRegister();
			serverTabs.insertTab(scene.getSceneTitle(), null, c, null, 0);
		}
	}

	@Override
	public void removeScene(IScene scene) {
		
		Component c = sceneToComponentMapping.get(scene);
		
		if(c != null) {
			scene.onUnregister();
			serverTabs.remove(c);
		}
	}

	@Override
	public void pack() {
	}

}
