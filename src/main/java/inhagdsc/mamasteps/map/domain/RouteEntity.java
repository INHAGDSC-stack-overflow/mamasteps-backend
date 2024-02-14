package inhagdsc.mamasteps.map.domain;

import inhagdsc.mamasteps.map.domain.converter.LatLngConverter;
import inhagdsc.mamasteps.map.dto.RouteDto;
import inhagdsc.mamasteps.user.entity.User;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "routes")
public class RouteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "route_name")
    private String routeName;

    @Column(name = "created_waypoint", columnDefinition = "json")
    @Convert(converter = LatLngConverter.class)
    private LatLng createdWaypoint;

    @Column(name = "poly-line", columnDefinition = "MEDIUMTEXT")
    private String polyLine;

    @Column(name = "total_distance_meters")
    private Double totalDistanceMeters;

    @Column(name = "total_time_seconds")
    private Integer totalTimeSeconds;

    @Column(name = "walk_speed")
    private double walkSpeed;

    @Column(name = "used_at", columnDefinition = "timestamp")
    private String usedAt;

    @Column(name = "evaluated_hardness")
    private int evaluatedHardness;

    @Column(name = "created_at", columnDefinition = "timestamp")
    private String createdAt;

    @Column(name = "updated_at", columnDefinition = "timestamp")
    private String updatedAt;

    public RouteDto toDto() {
        RouteDto dto = new RouteDto();
        dto.setRouteId(this.id);
        dto.setRouteName(this.routeName);
        dto.setCreatedWaypoint(this.createdWaypoint);
        dto.setPolyLine(this.polyLine);
        dto.setWalkSpeed(this.walkSpeed);
        dto.setUsedAt(this.usedAt);
        dto.setEvaluatedHardness(this.evaluatedHardness);
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

    public void setId(Long id) {
        this.id = id;
    }

    public LatLng getCreatedWaypoint() {
        return createdWaypoint;
    }

    public void setCreatedWaypoint(LatLng createdWaypoint) {
        this.createdWaypoint = createdWaypoint;
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

    public void setTotalDistanceMeters(Double totalDistanceMeters) {
        this.totalDistanceMeters = totalDistanceMeters;
    }

    public Integer getTotalTimeSeconds() {
        return totalTimeSeconds;
    }

    public void setTotalTimeSeconds(Integer totalTimeSeconds) {
        this.totalTimeSeconds = totalTimeSeconds;
    }

    public double getWalkSpeed() {
        return walkSpeed;
    }

    public void setWalkSpeed(double walkSpeed) {
        this.walkSpeed = walkSpeed;
    }

    public String getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(String usedAt) {
        this.usedAt = usedAt;
    }

    public int getEvaluatedHardness() {
        return evaluatedHardness;
    }

    public void setEvaluatedHardness(int evaluatedHardness) {
        this.evaluatedHardness = evaluatedHardness;
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

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }
}