package inhagdsc.mamasteps.map.dto;

import inhagdsc.mamasteps.map.domain.LatLng;

import java.util.List;

public class RouteRequestProfileDto {
    private Long id;
    private int targetTime;
    private double walkSpeed;
    private LatLng origin;
    private List<LatLng> startCloseWaypoints;
    private List<LatLng> endCloseWaypoints;
    private List<LatLng> createdWaypointCandidate;
    private String createdAt;
    private String updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(int targetTime) {
        this.targetTime = targetTime;
    }

    public double getWalkSpeed() {
        return walkSpeed;
    }

    public void setWalkSpeed(double walkSpeed) {
        this.walkSpeed = walkSpeed;
    }

    public LatLng getOrigin() {
        return origin;
    }

    public void setOrigin(LatLng origin) {
        this.origin = origin;
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

    public List<LatLng> getCreatedWaypointCandidate() {
        return createdWaypointCandidate;
    }

    public void setCreatedWaypointCandidate(List<LatLng> createdWaypointCandidate) {
        this.createdWaypointCandidate = createdWaypointCandidate;
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
