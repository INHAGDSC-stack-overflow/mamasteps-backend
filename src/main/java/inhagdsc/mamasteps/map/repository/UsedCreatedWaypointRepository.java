package inhagdsc.mamasteps.map.repository;

import inhagdsc.mamasteps.map.domain.LatLng;
import inhagdsc.mamasteps.map.domain.UsedCreatedWaypointEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsedCreatedWaypointRepository extends JpaRepository<UsedCreatedWaypointEntity, Long> {
    public List<UsedCreatedWaypointEntity> findAllByRoutesProfile(Long routesProfileId);
}
