package pub.terminal.coin.tradeinfo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
@AllArgsConstructor
@Builder
public class TradeData {

    public TradeData() {
    }

    @Column(name = "s_id")
    private Long id;

    private BigDecimal price;

    private BigDecimal amount;

    private Direction direction;

    private Long ts;

    public enum Direction {
        BUY, SELL
    }
}
