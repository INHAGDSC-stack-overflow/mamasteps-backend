package inhagdsc.mamasteps.map.domain;

import java.util.List;

public class RouteRequestDto {
    int targetTime;
    LatLng origin;
    List<LatLng> intermediates;

    public RouteRequestEntity toEntity() {
        RouteRequestEntity routeRequestEntity = new RouteRequestEntity();
        routeRequestEntity.setTargetTime(this.targetTime);
        routeRequestEntity.setOrigin(this.origin);
        routeRequestEntity.setIntermediates(this.intermediates);
        return routeRequestEntity;
    }

    public int getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(int targetTime) {
        this.targetTime = targetTime;
    }

    public LatLng getOrigin() {
        return origin;
    }

    public void setOrigin(LatLng origin) {
        this.origin = origin;
    }

    public List<LatLng> getIntermediates() {
        return intermediates;
    }

    public void setIntermediates(List<LatLng> intermediates) {
        this.intermediates = intermediates;
    }
}
