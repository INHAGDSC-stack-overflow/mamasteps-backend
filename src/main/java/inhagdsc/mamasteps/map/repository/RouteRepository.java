package inhagdsc.mamasteps.map.repository;

import inhagdsc.mamasteps.map.domain.RouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RouteRepository extends JpaRepository<RouteEntity, Long> {
    List<RouteEntity> findByUserId(Long userId);
    int countByUserId(Long userId);
    Optional<RouteEntity> findByIdAndUserId(Long id, Long userId);
}
