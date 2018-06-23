package pub.terminal.coin.tradeinfo.websocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public interface WebSocketClientAdapter {

    void onOpen(WebSocketClient client, ServerHandshake serverHandshake);

    void onMessage(WebSocketClient client, String s);

    void onClose(WebSocketClient client, int i, String s, boolean b);

    void onError(WebSocketClient client, Exception e);

    void subscribe(WebSocketClient client);
}
