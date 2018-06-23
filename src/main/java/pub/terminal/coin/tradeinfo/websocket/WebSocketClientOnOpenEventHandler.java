package pub.terminal.coin.tradeinfo.websocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public interface WebSocketClientOnOpenEventHandler {
    void onOpen(WebSocketClient client, ServerHandshake serverHandshake) ;
}
