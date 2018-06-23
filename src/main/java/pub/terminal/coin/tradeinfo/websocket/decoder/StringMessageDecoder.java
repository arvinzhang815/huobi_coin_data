package pub.terminal.coin.tradeinfo.websocket.decoder;

import java.io.IOException;

public interface StringMessageDecoder {
    String decode(byte[] bytes) throws IOException;
}
