package pub.terminal.coin.tradeinfo.websocket.config;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import pub.terminal.coin.tradeinfo.TradeInfoApplication;
import pub.terminal.coin.tradeinfo.websocket.WebSocketClientAdapter;
import pub.terminal.coin.tradeinfo.websocket.decoder.StringMessageDecoder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

@Component
@Configuration
public class WebSocketClientConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    public WebSocketClient getWebSocketClient(WebSocketClientAdapter adapter, @Qualifier("GZIP") StringMessageDecoder messageDecoder) throws URISyntaxException {
        WebSocketClient webSocketClient = new WebSocketClient(new URI(TradeInfoApplication.webSocketHost)) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                adapter.onOpen(this, serverHandshake);
            }

            @Override
            public void onMessage(String s) {
                adapter.onMessage(this, s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                adapter.onClose(this, i, s, b);
            }

            @Override
            public void onError(Exception e) {
                adapter.onError(this, e);
            }

            @Override
            public void onMessage(ByteBuffer bytes) {
                try {
                    onMessage(messageDecoder.decode(bytes.array()));
                } catch (IOException e) {
                    throw new RuntimeException("decode binary message error");
                }
            }

            @Override
            public void connect() {
                super.connect();
                retrySubscribe(this, adapter);

            }

            @Override
            public void reconnect() {
                super.reconnect();
                retrySubscribe(this, adapter);
            }

        };
        return webSocketClient;
    }

    private void retrySubscribe(WebSocketClient client, WebSocketClientAdapter webSocketClientAdapter) {
        while (true) {
            if (client.isOpen()) {
                webSocketClientAdapter.subscribe(client);
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
