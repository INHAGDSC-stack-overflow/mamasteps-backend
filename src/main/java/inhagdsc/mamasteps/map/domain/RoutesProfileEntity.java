package inhagdsc.mamasteps.map.domain;

import inhagdsc.mamasteps.map.domain.converter.LatLngListConverter;
import inhagdsc.mamasteps.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class RoutesProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "routes_profile_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String profile_name;

    @Column
    private int target_time;

    @Column(columnDefinition = "json")
    @Convert(converter = LatLngListConverter.class)
    private List<LatLng> waypoints_startclose;

    @Column(columnDefinition = "json")
    @Convert(converter = LatLngListConverter.class)
    private List<LatLng> waypoints_endclose;

    @Column(columnDefinition = "timestamp")
    private String created_at;

    @Column(columnDefinition = "timestamp")
    private String updated_at;
}
