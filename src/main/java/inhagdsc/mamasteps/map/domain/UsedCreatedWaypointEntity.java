package inhagdsc.mamasteps.map.domain;

import inhagdsc.mamasteps.map.domain.converter.LatLngConverter;
import jakarta.persistence.*;

@Entity
@Table(name = "used_created_waypoints")
public class UsedCreatedWaypointEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "waypoint_id", nullable = false)
    private Long waypointId;

    @ManyToOne
    @JoinColumn(name = "routes_profile_id", nullable = false)
    private RoutesProfileEntity routesProfile;

    @Column(name = "latlng", columnDefinition = "json")
    @Convert(converter = LatLngConverter.class)
    private LatLng latlng;

    public Long getWaypointId() {
        return waypointId;
    }

    public void setWaypointId(Long waypointId) {
        this.waypointId = waypointId;
    }

    public RoutesProfileEntity getRoutesProfile() {
        return routesProfile;
    }

    public void setRoutesProfile(RoutesProfileEntity routesProfile) {
        this.routesProfile = routesProfile;
    }

    public LatLng getLatlng() {
        return latlng;
    }

    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }
}
