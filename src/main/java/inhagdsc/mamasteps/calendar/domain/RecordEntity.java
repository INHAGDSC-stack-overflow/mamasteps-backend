package inhagdsc.mamasteps.calendar.domain;

import inhagdsc.mamasteps.calendar.dto.RecordDto;
import inhagdsc.mamasteps.map.domain.RouteEntity;
import inhagdsc.mamasteps.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Table(name = "records")
public class RecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "record_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = true)
    private RouteEntity route;

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
        Optional.ofNullable(this.getRoute())
                .map(RouteEntity::getId)
                .ifPresent(routeId -> {
                    dto.setRouteId(this.getRoute().getId());
                });
        dto.setDate(this.getStartAt());
        dto.setCompletedTimeSeconds(this.getCompletedTimeSeconds());
        dto.setCreatedAt(this.getCreatedAt());
        dto.setUpdatedAt(this.getUpdatedAt());
        return dto;
    }

    public void update(RecordEntity recordEntity) {
        this.route = recordEntity.getRoute();
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

    public User getUser() {
        return user;
    }

    public void setUser(User userId) {
        this.user = userId;
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

    public RouteEntity getRoute() {
        return route;
    }

    public void setRoute(RouteEntity routeId) {
        this.route = routeId;
    }

    public int getCompletedTimeSeconds() {
        return completedTimeSeconds;
    }

    public void setCompletedTimeSeconds(int completedTimeSeconds) {
        this.completedTimeSeconds = completedTimeSeconds;
    }
}
