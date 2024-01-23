package inhagdsc.mamasteps.map.service;

import com.fasterxml.jackson.databind.JsonNode;
import inhagdsc.mamasteps.map.domain.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PolylineEncoder {
    public String encode(JsonNode coordinates) throws IOException {
        List<LatLng> path = parseJsonToLatLngList(coordinates);

        return encode(path);
    }
    public String encode(List<LatLng> path) {
        long lastLat = 0;
        long lastLng = 0;

        StringBuilder result = new StringBuilder();

        for (LatLng point : path) {
            long lat = Math.round(point.getLatitude() * 1e5);
            long lng = Math.round(point.getLongitude() * 1e5);

            long dLat = lat - lastLat;
            long dLng = lng - lastLng;

            encode(dLat, result);
            encode(dLng, result);

            lastLat = lat;
            lastLng = lng;
        }

        return result.toString();
    }

    private static void encode(long v, StringBuilder result) {
        v = v < 0 ? ~(v << 1) : v << 1;
        while (v >= 0x20) {
            result.append(Character.toChars((int) ((0x20 | (v & 0x1f)) + 63)));
            v >>= 5;
        }
        result.append(Character.toChars((int) (v + 63)));
    }

    public static List<LatLng> parseJsonToLatLngList(JsonNode coordinates) throws IOException {
        JsonNode coordinatesNode = coordinates;
        List<LatLng> latLngList = new ArrayList<>();

        for (JsonNode coordinateNode : coordinatesNode) {
            double latitude = coordinateNode.path("latitude").asDouble();
            double longitude = coordinateNode.path("longitude").asDouble();
            latLngList.add(new LatLng(latitude, longitude));
        }

        return latLngList;
    }

}
