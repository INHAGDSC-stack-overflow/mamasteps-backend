package inhagdsc.mamasteps.map.dto.requestProfile;

import inhagdsc.mamasteps.map.domain.LatLng;
import inhagdsc.mamasteps.map.domain.RouteRequestProfileEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetRequestProfileResponse {
    private int targetTime;
    private double walkSpeed;
    private LatLng origin;
    private List<LatLng> startCloseWaypoints;
    private List<LatLng> endCloseWaypoints;
    private String createdAt;
    private String updatedAt;

    public GetRequestProfileResponse(RouteRequestProfileEntity routeRequestProfileEntity) {
        this.targetTime = routeRequestProfileEntity.getTargetTime();
        this.walkSpeed = routeRequestProfileEntity.getWalkSpeed();
        this.origin = routeRequestProfileEntity.getOrigin();
        this.startCloseWaypoints = routeRequestProfileEntity.getStartCloseWaypoints();
        this.endCloseWaypoints = routeRequestProfileEntity.getEndCloseWaypoints();
        this.createdAt = routeRequestProfileEntity.getCreatedAt();
        this.updatedAt = routeRequestProfileEntity.getUpdatedAt();
    }
}
