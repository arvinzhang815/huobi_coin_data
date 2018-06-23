package pub.terminal.coin.tradeinfo.monitor;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WebSocketClientConnectMonitor implements ApplicationContextAware {

    private ApplicationContext applicationContext;


    @Scheduled(initialDelay = 30000, fixedRate = 60000)
    public void monitor() {
        WebSocketClient webSocketClient = applicationContext.getBean(WebSocketClient.class);
        log.info("Webclient Opened: " + webSocketClient.isOpen());
        if (!webSocketClient.isOpen()) {
            webSocketClient.reconnect();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
