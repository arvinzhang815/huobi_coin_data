package pub.terminal.coin.tradeinfo.websocket;

import org.java_websocket.client.WebSocketClient;

public interface WebSocketClientOnErrorHandler {

    void onError(WebSocketClient client, Exception e);
}
