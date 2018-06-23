package pub.terminal.coin.tradeinfo.websocket;

import org.java_websocket.client.WebSocketClient;

public interface WebSocketClientOnCloseEventHandler {

    void onClose(WebSocketClient client, int i, String s, boolean b) ;
}
