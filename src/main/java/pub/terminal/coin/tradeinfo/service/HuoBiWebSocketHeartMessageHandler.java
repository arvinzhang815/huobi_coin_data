package pub.terminal.coin.tradeinfo.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pub.terminal.coin.tradeinfo.websocket.WebSocketClientMessageHandler;

@Component
@Slf4j
public class HuoBiWebSocketHeartMessageHandler implements WebSocketClientMessageHandler {

    @Override
    @Async
    public void onMessage(WebSocketClient client, String s) {
        Long timeStamp = JSONObject.parseObject(s).getLong("ping");
        JSONObject heartBeatAnswer = new JSONObject();
        heartBeatAnswer.put("pong", timeStamp);
        log.debug("响应心跳包..");
        client.send(heartBeatAnswer.toJSONString());
    }

    @Override
    public boolean canHandle(String s) {
        return canHandleHeartBeatMessage(s);
    }

    private boolean canHandleHeartBeatMessage(String s) {
        Long timeStamp = JSONObject.parseObject(s).getLong("ping");
        if (timeStamp != null) {
            return true;
        }
        return false;
    }
}
