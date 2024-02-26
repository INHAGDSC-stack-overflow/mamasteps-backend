package inhagdsc.mamasteps.map.repository;

import inhagdsc.mamasteps.map.domain.RouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComputingRouteRepository extends JpaRepository<RouteEntity, Long> {
    List<RouteEntity> findAllByUserId(Long userId);

    void deleteAllByUserId(Long userId);
}
