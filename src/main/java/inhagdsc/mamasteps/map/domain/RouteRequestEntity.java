package inhagdsc.mamasteps.map.domain;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RouteRequestEntity {
    int targetTime;
    LatLng origin;
    List<LatLng> intermediates;

    public JSONObject toGoogleJson() {
        JSONObject json = new JSONObject();

        json.put("origin", createLocationJson(this.origin));
        json.put("destination", createLocationJson(this.origin));

        JSONArray intermediatesJson = new JSONArray();
        for (LatLng latLng : this.intermediates) {
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
        for (LatLng latLng : this.intermediates) {
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

    public RouteRequestDto toDto() {
        RouteRequestDto routeRequestDto = new RouteRequestDto();
        routeRequestDto.setTargetTime(this.targetTime);
        routeRequestDto.setOrigin(this.origin);
        routeRequestDto.setIntermediates(this.intermediates);
        return routeRequestDto;
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
