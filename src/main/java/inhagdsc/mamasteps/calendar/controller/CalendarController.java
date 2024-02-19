package inhagdsc.mamasteps.calendar.controller;

import inhagdsc.mamasteps.calendar.domain.RecordEntity;
import inhagdsc.mamasteps.calendar.domain.ScheduleEntity;
import inhagdsc.mamasteps.calendar.dto.RecordDto;
import inhagdsc.mamasteps.calendar.dto.ScheduleDto;
import inhagdsc.mamasteps.calendar.service.CalendarService;
import inhagdsc.mamasteps.common.ApiResponse;
import inhagdsc.mamasteps.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static inhagdsc.mamasteps.common.code.status.ErrorStatus.FORBIDDEN;
import static inhagdsc.mamasteps.common.code.status.SuccessStatus.OK;

@RestController
@RequestMapping("/api/v1/calendar")
public class CalendarController {
    private final CalendarService calendarService;

    @Autowired
    public CalendarController(CalendarService scheduleService) {
        this.calendarService = scheduleService;
    }

    @GetMapping("/getNow")
    public ApiResponse<String> getNow() {
        return ApiResponse.onSuccess(OK, calendarService.getNow().toString());
    }

    @PostMapping("/addSchedule")
    public ApiResponse<Void> addSchedule(@AuthenticationPrincipal User user, @RequestBody ScheduleDto scheduleDto) {
        ScheduleEntity scheduleEntity = scheduleDto.toEntity();
        scheduleEntity.setUserId(user.getId());
        calendarService.addSchedule(scheduleEntity);
        return ApiResponse.onSuccess(OK, null);
    }

    @GetMapping("/getSchedules")
    public ApiResponse<List<ScheduleDto>> getSchedules(@AuthenticationPrincipal User user) {
        List<ScheduleDto> response = calendarService.getSchedules(user.getId());
        return ApiResponse.onSuccess(OK, response);
    }

    @PostMapping("/editSchedule/{scheduleId}")
    public ApiResponse<Void> editSchedule(@AuthenticationPrincipal User user, @PathVariable Long scheduleId, @RequestBody ScheduleDto scheduleDto) {
        ScheduleEntity scheduleEntity = scheduleDto.toEntity();
        scheduleEntity.setUserId(user.getId());
        try {
            calendarService.editSchedule(scheduleId, scheduleEntity);
            return ApiResponse.onSuccess(OK, null);
        } catch (Exception e) {
            return ApiResponse.onFailure(FORBIDDEN.getCode(), e.getMessage(), null);
        }
    }

    @DeleteMapping("/deleteSchedule/{scheduleId}")
    public ApiResponse<Void> deleteSchedule(@AuthenticationPrincipal User user, @PathVariable Long scheduleId) {
        try {
            calendarService.deleteSchedule(user.getId(), scheduleId);
            return ApiResponse.onSuccess(OK, null);
        } catch (Exception e) {
            return ApiResponse.onFailure(FORBIDDEN.getCode(), e.getMessage(), null);
        }
    }

    @PostMapping("/addRecord")
    public ApiResponse<Void> addRecord(@AuthenticationPrincipal User user, @RequestBody RecordDto recordDto) {
        RecordEntity recordEntity = recordDto.toEntity();
        recordEntity.setUserId(user.getId());
        calendarService.addRecord(recordEntity);
        return ApiResponse.onSuccess(OK, null);
    }

    @GetMapping("/getRecords")
    public ApiResponse<List<RecordDto>> getRecords(@AuthenticationPrincipal User user) {
        List<RecordDto> response = calendarService.getRecords(user.getId());
        return ApiResponse.onSuccess(OK, response);
    }

    @PostMapping("/editRecord/{recordId}")
    public ApiResponse<Void> editSchedule(@AuthenticationPrincipal User user, @PathVariable Long recordId, @RequestBody RecordDto recordDto) {
        RecordEntity recordEntity = recordDto.toEntity();
        recordEntity.setUserId(user.getId());
        try {
            calendarService.editRecord(recordId, recordEntity);
            return ApiResponse.onSuccess(OK, null);
        } catch (Exception e) {
            return ApiResponse.onFailure(FORBIDDEN.getCode(), e.getMessage(), null);
        }
    }

    @DeleteMapping("/deleteRecord/{recordId}")
    public ApiResponse<Void> deleteRecord(@AuthenticationPrincipal User user, @PathVariable Long recordId) {
        try {
            calendarService.deleteSchedule(user.getId(), recordId);
            return ApiResponse.onSuccess(OK, null);
        } catch (Exception e) {
            return ApiResponse.onFailure(FORBIDDEN.getCode(), e.getMessage(), null);
        }
    }

    @PostMapping("/createAutoSchedule")
    public ApiResponse<Void> createAutoSchedule(@AuthenticationPrincipal User user) {
        calendarService.createAutoSchedule(user);
        return ApiResponse.onSuccess(OK, null);
    }
}
