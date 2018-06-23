package pub.terminal.coin.tradeinfo.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import pub.terminal.coin.tradeinfo.model.Ask;
import pub.terminal.coin.tradeinfo.model.Bid;
import pub.terminal.coin.tradeinfo.model.MarketDepth;
import pub.terminal.coin.tradeinfo.repo.MarketDepthRepository;
import pub.terminal.coin.tradeinfo.websocket.WebSocketClientMessageHandler;
import pub.terminal.coin.tradeinfo.websocket.WebSocketMessageSubscriber;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class HuobiMarketDepthMessageHandler implements WebSocketMessageSubscriber, WebSocketClientMessageHandler {

    @Autowired
    private MarketDepthRepository marketDepthRepository;

    private static final JSONObject MARKET_DEPTH_SUB_MSG_TEMPLATE = JSONObject.parseObject("{\n" +
            "  \"sub\": \"\",\n" +
            "  \"id\": \"id10\"\n" +
            "}");

    private static final String ETH_USDT = "market.ethusdt.depth.step0";

    private static final String BTC_USDT = "market.btcusdt.depth.step0";

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
        marketDepthRepository.save(parse(s));
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
            MARKET_DEPTH_SUB_MSG_TEMPLATE.put("sub", s);
            client.send(MARKET_DEPTH_SUB_MSG_TEMPLATE.toJSONString());
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

    private MarketDepth parse(String json) {
        JSONObject object = JSONObject.parseObject(json);
        String value = object.getString(HANDLE_MSG_KEY);
        JSONObject tick = object.getJSONObject("tick");
        JSONArray bidsJsonArray = tick.getJSONArray("bids");
        JSONArray asksJsonArray = tick.getJSONArray("asks");

        List<Bid> bids = new ArrayList<>();
        List<Ask> asks = new ArrayList<>();

        for (Object o : bidsJsonArray) {
            JSONArray array = (JSONArray) o;
            BigDecimal price = array.getBigDecimal(0);
            BigDecimal amount = array.getBigDecimal(1);
            bids.add(new Bid(price, amount));
        }

        for (Object o : asksJsonArray) {
            JSONArray array = (JSONArray) o;
            BigDecimal price = array.getBigDecimal(0);
            BigDecimal amount = array.getBigDecimal(1);
            asks.add(new Ask(price, amount));
        }

        MarketDepth marketDepth = null;

        switch (value) {
            case ETH_USDT:
                marketDepth = MarketDepth.builder().type(MarketDepth.Type.ETH)
                        .timeStamp(object.getLong("ts"))
                        .asks(asks)
                        .bids(bids).build();
                break;
            case BTC_USDT:
                marketDepth = MarketDepth.builder().type(MarketDepth.Type.BTC)
                        .timeStamp(object.getLong("ts"))
                        .asks(asks)
                        .bids(bids).build();
                break;
        }

        return marketDepth;
    }


}
