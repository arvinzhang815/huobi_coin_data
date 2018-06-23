package pub.terminal.coin.tradeinfo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pub.terminal.coin.tradeinfo.model.KLinePoint;

@Repository
public interface KLinePointRepository extends JpaRepository<KLinePoint,Long> {

}
