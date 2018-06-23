package pub.terminal.coin.tradeinfo.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class Ask {

    private BigDecimal price;

    private BigDecimal amount;
}
