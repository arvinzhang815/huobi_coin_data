package pub.terminal.coin.tradeinfo.websocket.decoder;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;

@Component
@Qualifier("GZIP")
public class GzipBinaryMessageDecoder implements StringMessageDecoder {

    @Override
    public String decode(byte[] bytes) throws IOException {
        GZIPInputStream gis = null;
        gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
        String message = IOUtils.toString(gis, Charset.forName("UTF-8"));
        return message;
    }
}
