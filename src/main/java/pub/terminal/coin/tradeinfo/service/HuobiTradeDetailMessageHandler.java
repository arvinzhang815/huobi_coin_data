package pub.terminal.coin.tradeinfo.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import pub.terminal.coin.tradeinfo.model.TradeData;
import pub.terminal.coin.tradeinfo.model.TradeDetail;
import pub.terminal.coin.tradeinfo.repo.TradeDetailRepository;
import pub.terminal.coin.tradeinfo.websocket.WebSocketClientMessageHandler;
import pub.terminal.coin.tradeinfo.websocket.WebSocketMessageSubscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class HuobiTradeDetailMessageHandler implements WebSocketMessageSubscriber, WebSocketClientMessageHandler {

    @Autowired
    private TradeDetailRepository tradeDetailRepository;

    private static final JSONObject TRADE_DETAIL_SUB_MSG_TEMPLATE = JSONObject.parseObject("{\n" +
            "  \"sub\": \"\",\n" +
            "  \"id\": \"id10\"\n" +
            "}");

    private static final String ETH_USDT = "market.ethusdt.trade.detail";

    private static final String BTC_USDT = "market.btcusdt.trade.detail";


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
        tradeDetailRepository.save(parse(s));
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
            TRADE_DETAIL_SUB_MSG_TEMPLATE.put("sub", s);
            client.send(TRADE_DETAIL_SUB_MSG_TEMPLATE.toJSONString());
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

    private TradeDetail parse(String json) {
        JSONObject object = JSONObject.parseObject(json);
        Long timeStamp = object.getJSONObject("tick").getLong("ts");
        Long sId = object.getJSONObject("tick").getLong("id");
        List<TradeData> datas = new ArrayList<>();
        for (Object o : object.getJSONObject("tick").getJSONArray("data")) {
            JSONObject obj = (JSONObject) o;
            datas.add(TradeData.builder()
                            .id(obj.getLong("id"))
                            .ts(obj.getLong("ts"))
                            .direction(obj.getString("direction") == "sell" ? TradeData.Direction.SELL : TradeData.Direction.BUY)
                            .price(obj.getBigDecimal("price"))
                            .amount(obj.getBigDecimal("amount")).build());
        }
        return TradeDetail.builder().sId(sId).timeStamp(timeStamp).datas(datas).build();
    }


}
