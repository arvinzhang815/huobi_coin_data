package pub.terminal.coin.tradeinfo.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import pub.terminal.coin.tradeinfo.model.KLinePoint;
import pub.terminal.coin.tradeinfo.repo.KLinePointRepository;
import pub.terminal.coin.tradeinfo.websocket.WebSocketClientMessageHandler;
import pub.terminal.coin.tradeinfo.websocket.WebSocketMessageSubscriber;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class HuobiKLineMessageHandler implements WebSocketMessageSubscriber, WebSocketClientMessageHandler {

    @Autowired
    private KLinePointRepository kLinePointRepository;

    private static final JSONObject K_LINE_SUB_MSG_TEMPLATE = JSONObject.parseObject("{\n" +
            "  \"sub\": \"\",\n" +
            "  \"id\": \"id10\"\n" +
            "}");

    private static final String ETH_USDT = "market.ethusdt.kline.1min";

    private static final String BTC_USDT = "market.btcusdt.kline.1min";

    private static final String HANDLE_MSG_KEY = "ch";

    private static final Set<String> handMsgMap = new HashSet<String>() {{
        add(ETH_USDT);
        add(BTC_USDT);
    }};

    private static final String SUCCESS_SUB_KEY = "subbed";

    private Map<String, Boolean> kLineStatusMap = new HashMap<String, Boolean>() {{
        put(ETH_USDT, false);
        put(BTC_USDT, false);
    }};

    @Override
    @Async
    @Transactional
    public void onMessage(WebSocketClient client, String s) {
        log.debug(s);
        kLinePointRepository.save(parse(s));
    }

    @Override
    public boolean canHandle(String s) {
        String value = JSONObject.parseObject(s).getString(HANDLE_MSG_KEY);
        if (handMsgMap.contains(value)) {
            return true;
        }
        return false;
    }

    @Override
    public void subscribe(WebSocketClient client) {
        for (String s : handMsgMap) {
            K_LINE_SUB_MSG_TEMPLATE.put("sub", s);
            client.send(K_LINE_SUB_MSG_TEMPLATE.toJSONString());
        }
    }

    @Override
    public void unsubscribe(WebSocketClient client) {
        //TODO
    }

    @Override
    public void successSubscribe(String message) {
        String value = JSONObject.parseObject(message).getString(SUCCESS_SUB_KEY);
        if (!StringUtils.isEmpty(value)) {
            kLineStatusMap.put(value, true);
        }
    }

    private KLinePoint parse(String json) {
        JSONObject object = JSONObject.parseObject(json);
        String value = object.getString(HANDLE_MSG_KEY);
        JSONObject tick = object.getJSONObject("tick");
        KLinePoint point = null;
        switch (value) {
            case ETH_USDT:
                point = KLinePoint.builder()
                        .type(KLinePoint.Type.ETH)
                        .kLineId(tick.getLong("id"))
                        .amount(tick.getBigDecimal("amount"))
                        .open(tick.getBigDecimal("open"))
                        .close(tick.getBigDecimal("close"))
                        .high(tick.getBigDecimal("high"))
                        .low(tick.getBigDecimal("low"))
                        .count(tick.getInteger("count"))
                        .vol(tick.getBigDecimal("vol"))
                        .timeStamp(object.getLong("ts")).build();
                break;
            case BTC_USDT:
                point = KLinePoint.builder()
                        .type(KLinePoint.Type.BTC)
                        .kLineId(tick.getLong("id"))
                        .amount(tick.getBigDecimal("amount"))
                        .open(tick.getBigDecimal("open"))
                        .close(tick.getBigDecimal("close"))
                        .high(tick.getBigDecimal("high"))
                        .low(tick.getBigDecimal("low"))
                        .count(tick.getInteger("count"))
                        .vol(tick.getBigDecimal("vol"))
                        .timeStamp(object.getLong("ts")).build();
                break;
        }
        return point;
    }


}
