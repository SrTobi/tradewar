package tradewar.app.gui;

import java.awt.Component;
import java.util.Deque;
import java.util.LinkedList;

import javax.swing.JFrame;

import tradewar.api.IScene;
import tradewar.api.ISceneFrame;
import tradewar.utils.log.ILogStream;
import tradewar.utils.log.Log;

public class ApplicationWindow extends JFrame implements ISceneFrame {

	private static final long serialVersionUID = -2641960282979187364L;
	
	
	private Log log;
	private String windowName;
	private Deque<IScene> sceneStack;
	private Component currentView;

	public ApplicationWindow(ILogStream logStream, String windowName) {
		
		this.windowName = windowName;
		log = new Log(logStream, windowName);
		sceneStack = new LinkedList<>();
		setup();
	}
	
	private void setup() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 672, 503);
	}


	@Override
	public void setScene(IScene scene) {
		
		if(scene == null) {
			throw new IllegalArgumentException("controller must not be null!");
		}
		
		if(scene == sceneStack.peekLast())
			return;
		
		IScene topMost = sceneStack.peekLast();
		
		if(topMost != null) {
			topMost.onLeave();
		}
		
		if(sceneStack.contains(scene)) {
			// move component to the top
			sceneStack.remove(scene);
			
		} else {
			// send register message
			scene.onRegister();
		}

		sceneStack.addLast(scene);
		refreshTopMost();
	}

	@Override
	public void removeScene(IScene scene) {
	
		boolean wasTopMost = false;
		
		if(sceneStack.peekLast() == scene) {
			scene.onLeave();
			wasTopMost = true;
		}
		
		if(sceneStack.remove(scene)) {
			scene.onUnregister();
		}
		
		if(wasTopMost) {
			refreshTopMost();
		}
	}
	

	private void refreshTopMost() {
		IScene scene = sceneStack.getLast();
		
		scene.onEnter();
		
		if(currentView != null)
			remove(currentView);
		
		log.debug("Set topmost view[" + scene.getSceneName() + "] on window[" + getWindowName() + "]");
		
		currentView = scene.getView();
		add(currentView);
		
		validate();
		repaint();
	}

	public String getWindowName() {
		return windowName;
	}
}
