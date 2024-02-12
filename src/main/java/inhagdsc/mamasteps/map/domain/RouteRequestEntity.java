package inhagdsc.mamasteps.map.domain;

import inhagdsc.mamasteps.map.dto.RouteRequestDto;
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
    List<LatLng> startCloseIntermediates;
    List<LatLng> endCloseIntermediates;

    public JSONObject toGoogleJson() {
        JSONObject json = new JSONObject();

        json.put("origin", createLocationJson(this.origin));
        json.put("destination", createLocationJson(this.origin));

        JSONArray intermediatesJson = new JSONArray();
        for (LatLng latLng : this.startCloseIntermediates) {
            intermediatesJson.put(createLocationJson(latLng));
        }
        for (LatLng latLng : this.endCloseIntermediates) {
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
        for (LatLng latLng : this.startCloseIntermediates) {
            passList.add(Double.toString(latLng.getLongitude()) + "," + Double.toString(latLng.getLatitude()));
        }
        for (LatLng latLng : this.endCloseIntermediates) {
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
        routeRequestDto.setStartCloseIntermediates(this.startCloseIntermediates);
        routeRequestDto.setEndCloseIntermediates(this.endCloseIntermediates);
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

    public List<LatLng> getStartCloseIntermediates() {
        return startCloseIntermediates;
    }

    public void setStartCloseIntermediates(List<LatLng> startCloseIntermediates) {
        this.startCloseIntermediates = startCloseIntermediates;
    }

    public List<LatLng> getEndCloseIntermediates() {
        return endCloseIntermediates;
    }

    public void setEndCloseIntermediates(List<LatLng> endCloseIntermediates) {
        this.endCloseIntermediates = endCloseIntermediates;
    }
}
