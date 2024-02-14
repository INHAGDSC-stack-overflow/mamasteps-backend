package inhagdsc.mamasteps.calendar.domain;

import inhagdsc.mamasteps.calendar.dto.RecordDto;
import inhagdsc.mamasteps.map.domain.RouteEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "records")
public class RecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "record_id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @OneToOne
    @JoinColumn(name = "route_id", nullable = true)
    private RouteEntity route;

    @Column(name = "date", columnDefinition = "timestamp")
    private String date;

    @Column(name = "created_at", columnDefinition = "timestamp")
    private String createdAt;

    @Column(name = "updated_at", columnDefinition = "timestamp")
    private String updatedAt;

    public RecordDto toDto() {
        RecordDto dto = new RecordDto();
        dto.setId(this.getId());
        dto.setRoute(this.getRoute());
        dto.setDate(this.getDate());
        dto.setCreatedAt(this.getCreatedAt());
        dto.setUpdatedAt(this.getUpdatedAt());
        return dto;
    }

    public void update(RecordEntity recordEntity) {
        this.route = recordEntity.getRoute();
        this.date = recordEntity.getDate();
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
