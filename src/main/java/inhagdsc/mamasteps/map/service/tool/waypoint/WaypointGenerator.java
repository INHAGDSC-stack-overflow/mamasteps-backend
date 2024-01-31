package inhagdsc.mamasteps.map.service.tool.waypoint;

import inhagdsc.mamasteps.map.domain.LatLng;
import inhagdsc.mamasteps.map.domain.RouteRequestEntity;

import java.util.List;

public interface WaypointGenerator {
    public List<LatLng> getSurroundingWaypoints();
    public void setRouteRequestEntity(RouteRequestEntity routeRequestEntity);
}
