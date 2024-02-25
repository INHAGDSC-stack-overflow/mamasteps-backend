package inhagdsc.mamasteps.user.dto;

import inhagdsc.mamasteps.map.domain.LatLng;

public class SetOriginRequest {
    private LatLng origin;

    public LatLng getOrigin() {
        return origin;
    }

    public void setOrigin(LatLng origin) {
        this.origin = origin;
    }
}
