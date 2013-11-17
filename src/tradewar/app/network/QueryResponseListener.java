package tradewar.app.network;

public interface QueryResponseListener {
	public void onResponse(QueryResponse response);
	public void onSearchStop();
}
