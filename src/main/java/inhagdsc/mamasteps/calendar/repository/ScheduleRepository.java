package inhagdsc.mamasteps.calendar.repository;

import inhagdsc.mamasteps.calendar.domain.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {
    public List<ScheduleEntity> findAllByUserId(Long userId);
}
