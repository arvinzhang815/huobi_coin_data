package pub.terminal.coin.tradeinfo.websocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;


@Component
public class WebSocketEventHandlerHolder implements WebSocketClientAdapter {

    private List<WebSocketClientOnOpenEventHandler> openEventHandlers;
    private List<WebSocketClientMessageHandler> messageHandlers;
    private List<WebSocketClientOnCloseEventHandler> closeEventHandlers;
    private List<WebSocketClientOnErrorHandler> errorHandlers;
    private List<WebSocketMessageSubscriber> subscribers;

    @Autowired(required = false)
    public void setOpenEventHandlers(List<WebSocketClientOnOpenEventHandler> openEventHandlers) {
        this.openEventHandlers = openEventHandlers;
    }

    @Autowired(required = false)
    public void setMessageHandlers(List<WebSocketClientMessageHandler> messageHandlers) {
        this.messageHandlers = messageHandlers;
    }

    @Autowired(required = false)
    public void setCloseEventHandlers(List<WebSocketClientOnCloseEventHandler> closeEventHandlers) {
        this.closeEventHandlers = closeEventHandlers;
    }

    @Autowired(required = false)
    public void setErrorHandlers(List<WebSocketClientOnErrorHandler> errorHandlers) {
        this.errorHandlers = errorHandlers;
    }

    @Autowired(required = false)
    public void setSubscribers(List<WebSocketMessageSubscriber> subscribers) {
        this.subscribers = subscribers;
    }

    @Override
    public void onOpen(WebSocketClient client, ServerHandshake serverHandshake) {
        if (!CollectionUtils.isEmpty(openEventHandlers)) {
            for (WebSocketClientOnOpenEventHandler openEventHandler : openEventHandlers) {
                openEventHandler.onOpen(client, serverHandshake);
            }
        }
    }

    @Override
    public void onMessage(WebSocketClient client, String s) {
        if (!CollectionUtils.isEmpty(messageHandlers)) {
            for (WebSocketClientMessageHandler messageHandler : messageHandlers) {
                if (messageHandler.canHandle(s)) {
                    messageHandler.onMessage(client, s);
                }
            }
        }
        if (!CollectionUtils.isEmpty(subscribers)) {
            for (WebSocketMessageSubscriber subscriber : subscribers) {
               subscriber.successSubscribe(s);
            }
        }
    }

    @Override
    public void onClose(WebSocketClient client, int i, String s, boolean b) {
        if (!CollectionUtils.isEmpty(closeEventHandlers)) {
            for (WebSocketClientOnCloseEventHandler closeEventHandler : closeEventHandlers) {
                closeEventHandler.onClose(client, i, s, b);
            }
        }
    }

    @Override
    public void onError(WebSocketClient client, Exception e) {
        if (!CollectionUtils.isEmpty(errorHandlers)) {
            for (WebSocketClientOnErrorHandler errorHandler : errorHandlers) {
                errorHandler.onError(client, e);
            }
        }
    }

    @Override
    public void subscribe(WebSocketClient client) {
        if (!CollectionUtils.isEmpty(subscribers)) {
            for (WebSocketMessageSubscriber subscriber : subscribers) {
                subscriber.subscribe(client);
            }
        }
    }
}
