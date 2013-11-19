package tradewar.app.network;

public interface IQueryResponseListener {
	public void onResponse(QueryResponse response);
	public void onSearchStop();
}
