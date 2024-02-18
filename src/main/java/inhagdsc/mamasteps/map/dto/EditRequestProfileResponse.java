package inhagdsc.mamasteps.map.dto;

import inhagdsc.mamasteps.map.domain.LatLng;
import inhagdsc.mamasteps.map.domain.RouteRequestProfileEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EditRequestProfileResponse {
    private int targetTime;
    private double walkSpeed;
    private LatLng origin;
    private List<LatLng> startCloseWaypoints;
    private List<LatLng> endCloseWaypoints;
    private String createdAt;
    private String updatedAt;

    public EditRequestProfileResponse(RouteRequestProfileEntity routeRequestProfileEntity) {
        this.targetTime = routeRequestProfileEntity.getTargetTime();
        this.walkSpeed = routeRequestProfileEntity.getWalkSpeed();
        this.origin = routeRequestProfileEntity.getOrigin();
        this.startCloseWaypoints = routeRequestProfileEntity.getStartCloseWaypoints();
        this.endCloseWaypoints = routeRequestProfileEntity.getEndCloseWaypoints();
        this.createdAt = routeRequestProfileEntity.getCreatedAt();
        this.updatedAt = routeRequestProfileEntity.getUpdatedAt();
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
}
