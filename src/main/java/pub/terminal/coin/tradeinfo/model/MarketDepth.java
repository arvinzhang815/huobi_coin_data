package pub.terminal.coin.tradeinfo.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.util.List;

@Entity
@Data
@Table(name = "market_depth")
@Builder
public class MarketDepth {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long timeStamp;

    public enum Type{
        ETH, BTC
    }

    private Type type;

    @ElementCollection
    @CollectionTable(
            name = "bids",
            joinColumns = @JoinColumn(name = "market_id")
    )
    private List<Bid> bids;

    @ElementCollection
    @CollectionTable(
            name = "asks",
            joinColumns = @JoinColumn(name = "market_id")
    )
    private List<Ask> asks;

}
