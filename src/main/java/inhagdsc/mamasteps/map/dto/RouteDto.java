package inhagdsc.mamasteps.map.dto;

import inhagdsc.mamasteps.map.domain.LatLng;

public class RouteDto {
    private Long routeId;
    private Long routesProfileId;
    private LatLng createdWaypoint;
    private String polyLine;
    private Double totalDistanceMeters;
    private Integer totalTimeSeconds;
    private String createdAt;
    private String updatedAt;

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
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

    public void setCreatedWaypoint(LatLng createdWaypoint) {
        this.createdWaypoint = createdWaypoint;
    }

    public Long getRoutesProfileId() {
        return routesProfileId;
    }

    public void setRoutesProfileId(Long routesProfileId) {
        this.routesProfileId = routesProfileId;
    }
}
