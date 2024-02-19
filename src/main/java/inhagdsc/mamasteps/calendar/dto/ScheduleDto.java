package inhagdsc.mamasteps.calendar.dto;

import inhagdsc.mamasteps.calendar.domain.ScheduleEntity;

import java.time.LocalDateTime;

public class ScheduleDto {

    private Long id;
    private Long routeId;
    private LocalDateTime date;
    private int targetTimeSeconds;
    private String createdAt;
    private String updatedAt;

    public ScheduleEntity toEntity() {
        ScheduleEntity scheduleEntity = new ScheduleEntity();
        scheduleEntity.setId(id);
        scheduleEntity.setRouteId(routeId);
        scheduleEntity.setStartAt(date);
        scheduleEntity.setTargetTimeSeconds(targetTimeSeconds);
        scheduleEntity.setCreatedAt(createdAt);
        scheduleEntity.setUpdatedAt(updatedAt);
        return scheduleEntity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public int getTargetTimeSeconds() {
        return targetTimeSeconds;
    }

    public void setTargetTimeSeconds(int targetTimeSeconds) {
        this.targetTimeSeconds = targetTimeSeconds;
    }
}

