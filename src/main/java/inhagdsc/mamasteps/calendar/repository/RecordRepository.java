package inhagdsc.mamasteps.calendar.repository;

import inhagdsc.mamasteps.calendar.domain.RecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordRepository extends JpaRepository<RecordEntity, Long> {
    public List<RecordEntity> findAllByUserId(Long userId);
}
