package inhagdsc.mamasteps.map.domain;

import inhagdsc.mamasteps.map.domain.converter.LatLngConverter;
import inhagdsc.mamasteps.map.domain.converter.LatLngListConverter;
import inhagdsc.mamasteps.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "route_request_profiles")
public class RouteRequestProfileEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "target_time")
    private int targetTime;

    @Column(name = "walk_speed")
    private double walkSpeed;

    @Column(name = "origin", columnDefinition = "json")
    @Convert(converter = LatLngConverter.class)
    private LatLng origin;

    @Column(name = "waypoints_startclose", columnDefinition = "json")
    @Convert(converter = LatLngListConverter.class)
    private List<LatLng> startCloseWaypoints;

    @Column(name = "waypoints_endclose", columnDefinition = "json")
    @Convert(converter = LatLngListConverter.class)
    private List<LatLng> endCloseWaypoints;

    @Column(name = "created_waypoint_ candidate", columnDefinition = "json")
    @Convert(converter = LatLngListConverter.class)
    private List<LatLng> createdWaypointCandidates;

    @Column(name = "distance_factor")
    private double distanceFactor;

    @Column(name = "created_at", columnDefinition = "timestamp")
    private String createdAt;

    @Column(name = "updated_at", columnDefinition = "timestamp")
    private String updatedAt;
}
