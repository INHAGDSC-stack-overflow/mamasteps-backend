package inhagdsc.mamasteps.calendar.service;

import inhagdsc.mamasteps.calendar.domain.RecordEntity;
import inhagdsc.mamasteps.calendar.domain.ScheduleEntity;
import inhagdsc.mamasteps.calendar.dto.RecordDto;
import inhagdsc.mamasteps.calendar.dto.ScheduleDto;
import inhagdsc.mamasteps.calendar.repository.RecordRepository;
import inhagdsc.mamasteps.calendar.repository.ScheduleRepository;
import inhagdsc.mamasteps.map.domain.RouteEntity;
import inhagdsc.mamasteps.map.repository.RouteRepository;
import inhagdsc.mamasteps.user.entity.User;
import inhagdsc.mamasteps.user.entity.WalkPreference;
import inhagdsc.mamasteps.user.repository.UserRepository;
import inhagdsc.mamasteps.user.service.WorkoutOptimizeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CalendarServiceImpl implements CalendarService {

    private final ScheduleRepository scheduleRepository;
    private final RecordRepository recordRepository;
    private final WorkoutOptimizeService workoutOptimizeService;
    private final RouteRepository routeRepository;
    private final UserRepository userRepository;

    public CalendarServiceImpl(ScheduleRepository scheduleRepository, RecordRepository recordRepository, WorkoutOptimizeService workoutOptimizeService, RouteRepository routeRepository, UserRepository userRepository) {
        this.scheduleRepository = scheduleRepository;
        this.recordRepository = recordRepository;
        this.workoutOptimizeService = workoutOptimizeService;
        this.routeRepository = routeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public LocalDateTime getNow() {
        return LocalDateTime.now();
    }

    @Override
    public void addSchedule(User user, ScheduleDto scheduleDto) {
        ScheduleEntity scheduleEntity = scheduleDto.toEntity();
        scheduleEntity.setUser(user);
        Optional.ofNullable(scheduleDto.getRouteId())
                .ifPresent(routeId -> {
                    scheduleEntity.setRoute(routeRepository.findById(scheduleDto.getRouteId()).get());
                });
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
            if(targetScheduleEntity.getUser().getId().equals(scheduleEntity.getUser().getId())) {
                targetScheduleEntity.update(scheduleEntity);
                scheduleRepository.save(targetScheduleEntity);
            }
            else {
                throw new IllegalArgumentException("Schedule with ID:" + scheduleId + "not found for User ID:" + scheduleEntity.getUser());
            }
        });
    }

    @Override
    @Transactional
    public void deleteSchedule(User user, Long scheduleId) {
        scheduleRepository.findById(scheduleId).ifPresent(scheduleEntity -> {
            if (scheduleEntity.getUser().getId().equals(user.getId())) {
                scheduleRepository.delete(scheduleEntity);
            }
        });
    }

    @Override
    public void addRecord(User user, RecordDto recordDto) {
        RecordEntity recordEntity = recordDto.toEntity();
        recordEntity.setUser(user);
        Optional.ofNullable(recordDto.getRouteId())
                .ifPresent(routeId -> {
                    recordEntity.setRoute(routeRepository.findById(recordDto.getRouteId()).get());
                });
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
            if(targetRecordEntity.getUser().getId().equals(recordEntity.getUser().getId())) {
                targetRecordEntity.update(recordEntity);
                recordRepository.save(targetRecordEntity);
            }
            else {
                throw new IllegalArgumentException("Record with ID:" + recordId + "not found for User ID:" + recordEntity.getUser());
            }
        });
    }

    @Override
    @Transactional
    public void deleteRecord(User user, Long recordId) {
        recordRepository.findById(recordId).ifPresent(recordEntity -> {
            if(recordEntity.getUser().getId().equals(user.getId())) {
                recordRepository.delete(recordEntity);
            }
        });
    }

    @Override
    public void createAutoSchedule(User user) {
        int recommendedWalkTimeSeconds = user.getTargetTime();
        LocalDate now = LocalDate.from(getNow());
        DayOfWeek dayOfWeek = now.getDayOfWeek();
        userRepository.findUserWithWalkPreferences(user.getId()).ifPresent(userWithWalkPreferences -> {
            List<WalkPreference> preferences = userWithWalkPreferences.getWalkPreferences();
            for(WalkPreference preference : preferences) {
                ScheduleEntity scheduleEntity = new ScheduleEntity();
                LocalDate startDate = now.plusDays(calculateDayOfWeekDistance(preference.getDayOfWeek(), dayOfWeek) + 1);
                LocalTime startTime = preference.getStartTime();
                LocalDateTime startAt = LocalDateTime.of(startDate, startTime);
                if (scheduleRepository.existsByUserAndStartAt(user, startAt)) {
                    continue;
                }
                scheduleEntity.setUser(user);
                scheduleEntity.setStartAt(startAt);
                scheduleEntity.setTargetTimeSeconds(recommendedWalkTimeSeconds);
                scheduleEntity.setAutoGenerated(true);
                scheduleEntity.setCreatedAt(getNow().toString());
                scheduleEntity.setUpdatedAt(getNow().toString());
                scheduleRepository.save(scheduleEntity);
            }
        });
    }

    private int calculateDayOfWeekDistance(DayOfWeek dayOfWeek1, DayOfWeek dayOfWeek2) {
        int distance = dayOfWeek2.getValue() - dayOfWeek1.getValue();
        if(distance <= 0) {
            distance += 7;
        }
        return distance;
    }
}
