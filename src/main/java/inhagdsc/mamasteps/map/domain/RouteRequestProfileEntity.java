package inhagdsc.mamasteps.map.domain;

import inhagdsc.mamasteps.map.domain.converter.LatLngConverter;
import inhagdsc.mamasteps.map.domain.converter.LatLngListConverter;
import inhagdsc.mamasteps.user.entity.User;
import jakarta.persistence.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private List<LatLng> createdWaypointCandidate;

    @Column(name = "created_at", columnDefinition = "timestamp")
    private String createdAt;

    @Column(name = "updated_at", columnDefinition = "timestamp")
    private String updatedAt;

    public JSONObject toGoogleJson() {
        JSONObject json = new JSONObject();

        json.put("origin", createLocationJson(this.origin));
        json.put("destination", createLocationJson(this.origin));

        JSONArray intermediatesJson = new JSONArray();
        for (LatLng latLng : this.startCloseWaypoints) {
            intermediatesJson.put(createLocationJson(latLng));
        }
        for (LatLng latLng : this.endCloseWaypoints) {
            intermediatesJson.put(createLocationJson(latLng));
        }
        json.put("intermediates", intermediatesJson);

        return json;
    }

    public MultiValueMap<String, String> toTmapValueMap() {
        MultiValueMap<String, String> valueMap = new LinkedMultiValueMap<>();
        valueMap.add("startX", Double.toString(this.origin.getLongitude()));
        valueMap.add("startY", Double.toString(this.origin.getLatitude()));
        valueMap.add("endX", Double.toString(this.origin.getLongitude()));
        valueMap.add("endY", Double.toString(this.origin.getLatitude()));

        List<String> passList = new ArrayList<>();
        for (LatLng latLng : this.startCloseWaypoints) {
            passList.add(Double.toString(latLng.getLongitude()) + "," + Double.toString(latLng.getLatitude()));
        }
        for (LatLng latLng : this.endCloseWaypoints) {
            passList.add(Double.toString(latLng.getLongitude()) + "," + Double.toString(latLng.getLatitude()));
        }
        valueMap.add("passList", passList.stream().collect(Collectors.joining("_")));

        return valueMap;
    }

    private JSONObject createLocationJson(LatLng latLng) {
        JSONObject locationJson = new JSONObject();
        JSONObject latLngJson = new JSONObject();
        latLngJson.put("latitude", latLng.latitude);
        latLngJson.put("longitude", latLng.longitude);
        locationJson.put("latLng", latLngJson);
        return new JSONObject().put("location", locationJson);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public List<LatLng> getCreatedWaypointCandidate() {
        return createdWaypointCandidate;
    }

    public void setCreatedWaypointCandidate(List<LatLng> createdWaypointCandidate) {
        this.createdWaypointCandidate = createdWaypointCandidate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public double getWalkSpeed() {
        return walkSpeed;
    }

    public void setWalkSpeed(double walkSpeed) {
        this.walkSpeed = walkSpeed;
    }
}
