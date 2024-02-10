package inhagdsc.mamasteps.map.repository;

import inhagdsc.mamasteps.map.domain.RoutesProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoutesProfileRepository extends JpaRepository<RoutesProfileEntity, Long> {
    public RoutesProfileEntity findByUserId(Long userId);
    public List<RoutesProfileEntity> findAllByUserId(Long userId);
}
