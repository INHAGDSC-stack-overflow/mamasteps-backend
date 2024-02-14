package inhagdsc.mamasteps.calendar.service;

import inhagdsc.mamasteps.calendar.domain.RecordEntity;
import inhagdsc.mamasteps.calendar.domain.ScheduleEntity;
import inhagdsc.mamasteps.calendar.dto.RecordDto;
import inhagdsc.mamasteps.calendar.dto.ScheduleDto;
import inhagdsc.mamasteps.calendar.repository.RecordRepository;
import inhagdsc.mamasteps.calendar.repository.ScheduleRepository;
import inhagdsc.mamasteps.user.entity.User;
import inhagdsc.mamasteps.user.repository.UserRepository;
import inhagdsc.mamasteps.user.service.WorkoutOptimizeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalendarServiceImpl implements CalendarService {

    private final ScheduleRepository scheduleRepository;
    private final RecordRepository recordRepository;
    private final WorkoutOptimizeService workoutOptimizeService;
    private final UserRepository userRepository;

    public CalendarServiceImpl(ScheduleRepository scheduleRepository, RecordRepository recordRepository, WorkoutOptimizeService workoutOptimizeService, UserRepository userRepository) {
        this.scheduleRepository = scheduleRepository;
        this.recordRepository = recordRepository;
        this.workoutOptimizeService = workoutOptimizeService;
        this.userRepository = userRepository;
    }

    @Override
    public LocalDateTime getNow() {
        return LocalDateTime.now();
    }

    @Override
    public void addSchedule(ScheduleEntity scheduleEntity) {
        scheduleRepository.save(scheduleEntity);
    }

    @Override
    public List<ScheduleDto> getSchedules(Long userId) {
        List<ScheduleEntity> scheduleEntities = scheduleRepository.findAllByUserId(userId);
        List<ScheduleDto> scheduleDtos = new ArrayList<>();
        for(ScheduleEntity scheduleEntity : scheduleEntities) {
            scheduleDtos.add(scheduleEntity.toDto());
        }
        return scheduleDtos;
    }

    @Override
    public void editSchedule(Long scheduleId, ScheduleEntity scheduleEntity) {
        scheduleRepository.findById(scheduleId).ifPresent(targetScheduleEntity -> {
            if(targetScheduleEntity.getUserId().equals(scheduleEntity.getUserId())) {
                targetScheduleEntity.update(scheduleEntity);
                scheduleRepository.save(targetScheduleEntity);
            }
            else {
                throw new IllegalArgumentException("Schedule with ID:" + scheduleId + "not found for User ID:" + scheduleEntity.getUserId());
            }
        });
    }

    @Override
    @Transactional
    public void deleteSchedule(Long userId, Long scheduleId) {
        scheduleRepository.findById(scheduleId).ifPresent(scheduleEntity -> {
            if(scheduleEntity.getUserId().equals(userId)) {
                scheduleRepository.delete(scheduleEntity);
            }
            else {
                throw new IllegalArgumentException("Schedule with ID:" + scheduleId + "not found for User ID:" + userId);
            }
        });
    }

    @Override
    public void addRecord(RecordEntity recordEntity) {
        User user = userRepository.findById(recordEntity.getUserId()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        recordRepository.save(recordEntity);
        user.setWalkCount(user.getWalkCount() + 1);
        if (user.getWalkCount() % 5 == 0) {
            workoutOptimizeService.increaseTargetTime(user);
        }
    }

    @Override
    public List<RecordDto> getRecords(Long userId) {
        List<RecordEntity> recordEntities = recordRepository.findAllByUserId(userId);
        List<RecordDto> recordDtos = new ArrayList<>();
        for(RecordEntity recordEntity : recordEntities) {
            recordDtos.add(recordEntity.toDto());
        }
        return recordDtos;
    }

    @Override
    public void editRecord(Long recordId, RecordEntity recordEntity) {
        recordRepository.findById(recordId).ifPresent(targetRecordEntity -> {
            if(targetRecordEntity.getUserId().equals(recordEntity.getUserId())) {
                targetRecordEntity.update(recordEntity);
                recordRepository.save(targetRecordEntity);
            }
            else {
                throw new IllegalArgumentException("Record with ID:" + recordId + "not found for User ID:" + recordEntity.getUserId());
            }
        });
    }

    @Override
    @Transactional
    public void deleteRecord(Long userId, Long recordId) {
        recordRepository.findById(recordId).ifPresent(recordEntity -> {
            if(recordEntity.getUserId().equals(userId)) {
                recordRepository.delete(recordEntity);
            }
            else {
                throw new IllegalArgumentException("Record with ID:" + recordId + "not found for User ID:" + userId);
            }
        });
    }
}
