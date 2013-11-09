package tradewar.api;

import java.awt.Component;


public interface IScene {
	
	public Component getView();
	public String getSceneName();
	public String getSceneTitle();

	public void onRegister();
	public void onUnregister();
	public void onEnter();
	public void onLeave();
}
