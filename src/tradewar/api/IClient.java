package tradewar.api;

public interface IClient {

	void start(ISceneFrame frame, String nickname, ISocket connection);
	void stop();
}
