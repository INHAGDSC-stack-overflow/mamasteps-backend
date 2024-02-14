package inhagdsc.mamasteps.calendar.service;

import inhagdsc.mamasteps.calendar.domain.RecordEntity;
import inhagdsc.mamasteps.calendar.domain.ScheduleEntity;
import inhagdsc.mamasteps.calendar.dto.RecordDto;
import inhagdsc.mamasteps.calendar.dto.ScheduleDto;

import java.time.LocalDateTime;
import java.util.List;

public interface CalendarService {
    LocalDateTime getNow();

    void addSchedule(ScheduleEntity scheduleEntity);

    List<ScheduleDto> getSchedules(Long userId);

    void editSchedule(Long scheduleId, ScheduleEntity scheduleEntity);

    void deleteSchedule(Long userId, Long scheduleId);

    void addRecord(RecordEntity recordEntity);

    List<RecordDto> getRecords(Long userId);

    void editRecord(Long recordId, RecordEntity recordEntity);

    void deleteRecord(Long userId, Long recordId);
}
