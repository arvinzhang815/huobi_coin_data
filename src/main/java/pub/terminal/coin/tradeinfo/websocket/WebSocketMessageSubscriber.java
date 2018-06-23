package pub.terminal.coin.tradeinfo.websocket;

import org.java_websocket.client.WebSocketClient;

public interface WebSocketMessageSubscriber {

    void subscribe(WebSocketClient client);

    void unsubscribe(WebSocketClient client);

    void successSubscribe(String message);

}
