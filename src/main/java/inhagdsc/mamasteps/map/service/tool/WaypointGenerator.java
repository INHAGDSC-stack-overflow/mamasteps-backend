package inhagdsc.mamasteps.map.service.tool;

import inhagdsc.mamasteps.map.domain.LatLng;
import inhagdsc.mamasteps.map.domain.RouteRequestProfileEntity;

import java.util.List;

public interface WaypointGenerator {
    public List<LatLng> getSurroundingWaypoints(RouteRequestProfileEntity requestProfileEntity);
}
