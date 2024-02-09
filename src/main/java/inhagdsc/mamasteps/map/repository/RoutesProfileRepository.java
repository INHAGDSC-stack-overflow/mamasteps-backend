package inhagdsc.mamasteps.map.repository;

import inhagdsc.mamasteps.map.domain.RoutesProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoutesProfileRepository extends JpaRepository<RoutesProfileEntity, Long> {
    Optional<List<RoutesProfileEntity>> findByUser_Email(String email);
}
