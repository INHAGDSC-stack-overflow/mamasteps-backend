package inhagdsc.mamasteps.map.domain;

import inhagdsc.mamasteps.map.domain.converter.LatLngConverter;
import inhagdsc.mamasteps.map.dto.RouteDto;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "routes")
public class RouteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "route_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "routes_profile_id", nullable = false)
    private RoutesProfileEntity routesProfile;

    @Column(name = "created_waypoint", columnDefinition = "json")
    @Convert(converter = LatLngConverter.class)
    private LatLng createdWaypoint;

    @Column(name = "poly-line", columnDefinition = "MEDIUMTEXT")
    private String polyLine;

    @Column(name = "total_distance_meters")
    private Double totalDistanceMeters;

    @Column(name = "total_time_seconds")
    private Integer totalTimeSeconds;

    @Column(name = "created_at", columnDefinition = "timestamp")
    private String createdAt;

    @Column(name = "updated_at", columnDefinition = "timestamp")
    private String updatedAt;

    public RouteDto toDto() {
        RouteDto dto = new RouteDto();
        dto.setRouteId(this.id);
        dto.setRoutesProfileId(this.routesProfile.getId());
        dto.setCreatedWaypoint(this.createdWaypoint);
        dto.setPolyLine(this.polyLine);
        dto.setTotalDistanceMeters(this.totalDistanceMeters);
        dto.setTotalTimeSeconds(this.totalTimeSeconds);
        dto.setCreatedAt(this.createdAt);
        dto.setUpdatedAt(this.updatedAt);
        return dto;
    }

    public static List<RouteDto> toDtoList(List<RouteEntity> originalList) {
        List<RouteDto> dtos = new ArrayList<>();
        for (RouteEntity item : originalList) {
            dtos.add(item.toDto());
        }
        return dtos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long routeId) {
        this.id = routeId;
    }

    public RoutesProfileEntity getRoutesProfile() {
        return routesProfile;
    }

    public void setRoutesProfile(RoutesProfileEntity routesProfile) {
        this.routesProfile = routesProfile;
    }

    public String getPolyLine() {
        return polyLine;
    }

    public void setPolyLine(String polyLine) {
        this.polyLine = polyLine;
    }

    public Double getTotalDistanceMeters() {
        return totalDistanceMeters;
    }

    public void setTotalDistanceMeters(Double pathDistance) {
        this.totalDistanceMeters = pathDistance;
    }

    public Integer getTotalTimeSeconds() {
        return totalTimeSeconds;
    }

    public void setTotalTimeSeconds(Integer requiredTime) {
        this.totalTimeSeconds = requiredTime;
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

    public LatLng getCreatedWaypoint() {
        return createdWaypoint;
    }

    public void setCreatedWaypoint(LatLng latlng) {
        this.createdWaypoint = latlng;
    }
}