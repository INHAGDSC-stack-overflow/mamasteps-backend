package inhagdsc.mamasteps.map.repository;

import inhagdsc.mamasteps.map.domain.RouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteRepository extends JpaRepository<RouteEntity, Long> {
    public List<RouteEntity> findAllByRoutesProfile_Id(Long routesProfileId);
}
