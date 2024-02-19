package inhagdsc.mamasteps.calendar.repository;

import inhagdsc.mamasteps.calendar.domain.ScheduleEntity;
import inhagdsc.mamasteps.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {
    public List<ScheduleEntity> findAllByUserId(Long userId);

    @Query("SELECT COUNT(s) > 0 FROM ScheduleEntity s WHERE s.userId = :userId AND s.startAt = :startAt")
    boolean existsByUserIdAndStartAt(Long userId, LocalDateTime startAt);
}
