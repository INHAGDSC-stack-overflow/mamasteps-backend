package inhagdsc.mamasteps.map.dto;

import inhagdsc.mamasteps.map.domain.LatLng;
import jakarta.persistence.Column;

public class RouteDto {
    private Long routeId;
    private LatLng createdWaypoint;
    private String routeName;
    private String polyLine;
    private Double totalDistanceMeters;
    private Integer totalTimeSeconds;
    private Double walkSpeed;
    private String usedAt;
    private int evaluatedHardness;
    private String createdAt;
    private String updatedAt;

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
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

    public Double getWalkSpeed() {
        return walkSpeed;
    }

    public void setWalkSpeed(Double walkSpeed) {
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

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }
}
