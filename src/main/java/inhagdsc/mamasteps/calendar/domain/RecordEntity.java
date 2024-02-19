package inhagdsc.mamasteps.calendar.domain;

import inhagdsc.mamasteps.calendar.dto.RecordDto;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "records")
public class RecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "record_id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "route_id", nullable = true)
    private Long routeId;

    @Column(name = "start-at")
    private LocalDateTime startAt;

    @Column(name = "completed_time_seconds")
    private int completedTimeSeconds;

    @Column(name = "created_at", columnDefinition = "timestamp")
    private String createdAt;

    @Column(name = "updated_at", columnDefinition = "timestamp")
    private String updatedAt;

    public RecordDto toDto() {
        RecordDto dto = new RecordDto();
        dto.setId(this.getId());
        dto.setRouteId(this.getRouteId());
        dto.setDate(this.getStartAt());
        dto.setCompletedTimeSeconds(this.getCompletedTimeSeconds());
        dto.setCreatedAt(this.getCreatedAt());
        dto.setUpdatedAt(this.getUpdatedAt());
        return dto;
    }

    public void update(RecordEntity recordEntity) {
        this.routeId = recordEntity.getRouteId();
        this.startAt = recordEntity.getStartAt();
        this.completedTimeSeconds = recordEntity.getCompletedTimeSeconds();
        this.updatedAt = recordEntity.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDateTime date) {
        this.startAt = date;
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
