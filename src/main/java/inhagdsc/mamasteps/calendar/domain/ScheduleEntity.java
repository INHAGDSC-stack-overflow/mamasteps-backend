package inhagdsc.mamasteps.calendar.domain;

import inhagdsc.mamasteps.calendar.dto.ScheduleDto;
import inhagdsc.mamasteps.map.domain.RouteEntity;
import inhagdsc.mamasteps.user.entity.User;
import jakarta.persistence.*;
@Entity
@Table(name = "schedules")
public class ScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "schedule_id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "route_id", nullable = true)
    private Long routeId;

    @Column(name = "date", columnDefinition = "timestamp")
    private String date;

    @Column(name = "target_time_seconds")
    private int targetTimeSeconds;

    @Column(name = "created_at", columnDefinition = "timestamp")
    private String createdAt;

    @Column(name = "updated_at", columnDefinition = "timestamp")
    private String updatedAt;

    public ScheduleDto toDto() {
        ScheduleDto dto = new ScheduleDto();
        dto.setId(this.getId());
        dto.setRouteId(this.getRouteId());
        dto.setDate(this.getDate());
        dto.setTargetTimeSeconds(this.getTargetTimeSeconds());
        dto.setCreatedAt(this.getCreatedAt());
        dto.setUpdatedAt(this.getUpdatedAt());
        return dto;
    }

    public void update(ScheduleEntity scheduleEntity) {
        this.routeId = scheduleEntity.getRouteId();
        this.date = scheduleEntity.getDate();
        this.updatedAt = scheduleEntity.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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