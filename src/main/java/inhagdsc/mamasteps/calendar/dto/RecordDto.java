package inhagdsc.mamasteps.calendar.dto;

import inhagdsc.mamasteps.calendar.domain.RecordEntity;

import java.time.LocalDateTime;

public class RecordDto {
    private Long id;
    private Long routeId;
    private LocalDateTime date;
    private int completedTimeSeconds;
    private String createdAt;
    private String updatedAt;

    public RecordEntity toEntity() {
        RecordEntity recordEntity = new RecordEntity();
        recordEntity.setId(id);
        recordEntity.setRouteId(routeId);
        recordEntity.setStartAt(date);
        recordEntity.setCompletedTimeSeconds(completedTimeSeconds);
        recordEntity.setCreatedAt(createdAt);
        recordEntity.setUpdatedAt(updatedAt);
        return recordEntity;
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

    public int getCompletedTimeSeconds() {
        return completedTimeSeconds;
    }

    public void setCompletedTimeSeconds(int completedTimeSeconds) {
        this.completedTimeSeconds = completedTimeSeconds;
    }
}

