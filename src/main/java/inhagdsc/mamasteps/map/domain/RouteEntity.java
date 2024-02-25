package inhagdsc.mamasteps.map.domain;

import inhagdsc.mamasteps.map.domain.converter.LatLngConverter;
import inhagdsc.mamasteps.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "routes")
public class RouteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "route_name")
    private String routeName;

    @Column(name = "created_waypoint", columnDefinition = "json")
    @Convert(converter = LatLngConverter.class)
    private LatLng createdWaypoint;

    @Column(name = "poly-line", columnDefinition = "MEDIUMTEXT")
    private String polyLine;

    @Column(name = "total_distance_meters")
    private Double totalDistanceMeters;

    @Column(name = "total_time_seconds")
    private Integer totalTimeSeconds;

    @Column(name = "walk_speed")
    private double walkSpeed;

    @Column(name = "used_at", columnDefinition = "timestamp")
    private String usedAt;

    @Column(name = "evaluated_hardness")
    private int evaluatedHardness;

    @Column(name = "created_at", columnDefinition = "timestamp")
    private String createdAt;

    @Column(name = "updated_at", columnDefinition = "timestamp")
    private String updatedAt;
}