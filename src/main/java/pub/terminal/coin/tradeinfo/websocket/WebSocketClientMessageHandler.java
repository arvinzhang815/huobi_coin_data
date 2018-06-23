package pub.terminal.coin.tradeinfo.websocket;

import org.java_websocket.client.WebSocketClient;

public interface WebSocketClientMessageHandler {

    void onMessage(WebSocketClient client, String s);

    boolean canHandle(String s);
}
