package pub.terminal.coin.tradeinfo.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "k_line_point")
@Builder
public class KLinePoint {

    public enum Type{
        ETH, BTC
    }

    //主键
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long kLineId;

    //币种
    private Type type;

    //交易时刻
    private Long timeStamp;

    //开盘价
    private BigDecimal open;

    //收盘价
    private BigDecimal close;

    //最低价
    private BigDecimal low;

    //最高价
    private BigDecimal high;

    //交易量
    private BigDecimal amount;

    //交易总额
    private BigDecimal vol;

    //交易笔数
    private Integer count;

}
