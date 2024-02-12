package inhagdsc.mamasteps.map.domain;

import inhagdsc.mamasteps.map.domain.converter.LatLngConverter;
import inhagdsc.mamasteps.map.domain.converter.LatLngListConverter;
import inhagdsc.mamasteps.map.dto.RoutesProfileDto;
import inhagdsc.mamasteps.user.entity.User;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "routes_profile")
public class RoutesProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "routes_profile_id", nullable = false)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "profile_name")
    private String profileName;
    @Column(name = "target_time")
    private int targetTime;
    @Column(name = "origin", columnDefinition = "json")
    @Convert(converter = LatLngConverter.class)
    private LatLng origin;
    @Column(name = "waypoints_startclose", columnDefinition = "json")
    @Convert(converter = LatLngListConverter.class)
    private List<LatLng> startCloseWaypoints;
    @Column(name = "waypoints_endclose", columnDefinition = "json")
    @Convert(converter = LatLngListConverter.class)
    private List<LatLng> endCloseWaypoints;
    @Column(name = "created_at", columnDefinition = "timestamp")
    private String createdAt;
    @Column(name = "updated_at",columnDefinition = "timestamp")
    private String updatedAt;

    public RoutesProfileDto toDto() {
        RoutesProfileDto dto = new RoutesProfileDto();
        dto.setId(this.getId());
        dto.setProfileName(this.getProfileName());
        dto.setTargetTime(this.getTargetTime());
        dto.setOrigin(this.getOrigin());
        dto.setStartCloseWaypoints(this.getStartCloseWaypoints());
        dto.setEndCloseWaypoints(this.getEndCloseWaypoints());
        dto.setCreatedAt(this.getCreatedAt());
        dto.setUpdatedAt(this.getUpdatedAt());
        return dto;
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

    public void setUser(User user) {
        this.user = user;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public int getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(int targetTime) {
        this.targetTime = targetTime;
    }

    public List<LatLng> getStartCloseWaypoints() {
        return startCloseWaypoints;
    }

    public void setStartCloseWaypoints(List<LatLng> startCloseWaypoints) {
        this.startCloseWaypoints = startCloseWaypoints;
    }

    public List<LatLng> getEndCloseWaypoints() {
        return endCloseWaypoints;
    }

    public void setEndCloseWaypoints(List<LatLng> endCloseWaypoints) {
        this.endCloseWaypoints = endCloseWaypoints;
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

    public LatLng getOrigin() {
        return origin;
    }

    public void setOrigin(LatLng origin) {
        this.origin = origin;
    }
}
