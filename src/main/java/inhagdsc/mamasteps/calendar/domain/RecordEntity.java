package inhagdsc.mamasteps.calendar.domain;

import inhagdsc.mamasteps.calendar.dto.RecordDto;
import inhagdsc.mamasteps.map.domain.RouteEntity;
import inhagdsc.mamasteps.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Getter
@Setter
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
}
