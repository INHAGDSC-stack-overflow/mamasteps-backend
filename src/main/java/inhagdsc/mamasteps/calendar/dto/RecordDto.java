package inhagdsc.mamasteps.calendar.dto;

import inhagdsc.mamasteps.calendar.domain.RecordEntity;
import inhagdsc.mamasteps.calendar.domain.ScheduleEntity;
import inhagdsc.mamasteps.map.domain.RouteEntity;

public class RecordDto {
    private Long id;
    private RouteEntity route;
    private String date;
    private String createdAt;
    private String updatedAt;

    public RecordEntity toEntity() {
        RecordEntity scheduleEntity = new RecordEntity();
        scheduleEntity.setId(id);
        scheduleEntity.setRoute(route);
        scheduleEntity.setDate(date);
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

    public RouteEntity getRoute() {
        return route;
    }

    public void setRoute(RouteEntity route) {
        this.route = route;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
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
}

