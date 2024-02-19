package inhagdsc.mamasteps.map.repository;

import inhagdsc.mamasteps.map.domain.RouteRequestProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RouteRequestProfileRepository extends JpaRepository<RouteRequestProfileEntity, Long> {
    Optional<RouteRequestProfileEntity> findByUserId(Long userId);
}
