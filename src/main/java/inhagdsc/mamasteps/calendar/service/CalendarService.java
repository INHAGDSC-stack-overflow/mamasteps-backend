package inhagdsc.mamasteps.calendar.service;

import inhagdsc.mamasteps.calendar.domain.RecordEntity;
import inhagdsc.mamasteps.calendar.domain.ScheduleEntity;
import inhagdsc.mamasteps.calendar.dto.RecordDto;
import inhagdsc.mamasteps.calendar.dto.ScheduleDto;
import inhagdsc.mamasteps.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface CalendarService {
    LocalDateTime getNow();

    void addSchedule(User user, ScheduleDto scheduleDto);

    List<ScheduleDto> getSchedules(Long userId);

    void editSchedule(Long scheduleId, ScheduleEntity scheduleEntity);

    void deleteSchedule(User user, Long scheduleId);

    void addRecord(User user, RecordDto recordDto);

    List<RecordDto> getRecords(Long userId);

    void editRecord(Long recordId, RecordEntity recordEntity);

    void deleteRecord(User user, Long recordId);

    void createAutoSchedule(User user);
}
