package inhagdsc.mamasteps.map.dto;

import inhagdsc.mamasteps.map.domain.LatLng;

import java.util.List;

public class RoutesProfileDto {
    private Long id;
    private String profileName;
    private int targetTime;
    private LatLng origin;
    private List<LatLng> startCloseWaypoints;
    private List<LatLng> endCloseWaypoints;
    private String createdAt;
    private String updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
