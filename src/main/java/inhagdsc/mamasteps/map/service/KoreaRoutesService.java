package inhagdsc.mamasteps.map.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import inhagdsc.mamasteps.map.domain.LatLng;
import inhagdsc.mamasteps.map.domain.RouteRequestDto;
import inhagdsc.mamasteps.map.domain.RouteRequestEntity;
import inhagdsc.mamasteps.map.service.tool.PolylineEncoder;
import inhagdsc.mamasteps.map.service.tool.waypoint.WaypointGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@Profile("korea")
public class KoreaRoutesService implements RoutesService {
    @Value("${TMAP_API_KEY}")
    private String apiKey;
    private final WebClient webClient;
    private final WaypointGenerator waypointGenerator;

    @Autowired
    public KoreaRoutesService(WebClient.Builder webClientBuilder, WaypointGenerator waypointGenerator) {
        this.webClient = webClientBuilder.baseUrl("https://apis.openapi.sk.com").build();
        this.waypointGenerator = waypointGenerator;
    }

    @Override
    public ObjectNode computeRoutes(RouteRequestDto routeRequestDto) throws IOException {
        RouteRequestEntity originRouteRequestEntity = routeRequestDto.toEntity();
        waypointGenerator.setRouteRequestEntity(originRouteRequestEntity);
        boolean timeOverflow = false;
        List<LatLng> createdWaypoints = waypointGenerator.getSurroundingWaypoints();
        if (createdWaypoints.isEmpty()) {
            timeOverflow = true;
            createdWaypoints.add(originRouteRequestEntity.getOrigin());
        }

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode result = mapper.createObjectNode();
        ArrayNode routesArray = mapper.createArrayNode();
        for (LatLng waypoint : createdWaypoints) {
            RouteRequestEntity routeRequestEntity = new RouteRequestEntity();
            routeRequestEntity.setTargetTime(originRouteRequestEntity.getTargetTime());
            routeRequestEntity.setOrigin(originRouteRequestEntity.getOrigin().clone());
            routeRequestEntity.setStartCloseIntermediates(LatLng.deepCopyList(originRouteRequestEntity.getStartCloseIntermediates()));
            routeRequestEntity.setEndCloseIntermediates(LatLng.deepCopyList(originRouteRequestEntity.getEndCloseIntermediates()));
            if (!timeOverflow) {
                routeRequestEntity.getStartCloseIntermediates().add(waypoint);
            }

            MultiValueMap<String, String> requestBody = buildRequestBody(routeRequestEntity);
            try {
                ObjectNode route = getRoute(parseApiResponse(postAPIRequest(requestBody)));
                routesArray.add(route);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        ArrayNode sortedRoutesArray = sortRoutesArrayByTime(routesArray);
        deduplicateRoutes(sortedRoutesArray);
        result.put("timeOverflow", timeOverflow);
        result.put("polylines", sortedRoutesArray);
        return result;
    }

    private MultiValueMap<String, String> buildRequestBody(RouteRequestEntity routeRequestEntity) {
        MultiValueMap<String, String> formData = routeRequestEntity.toTmapValueMap();
        formData.add("speed", "35");
        formData.add("startName", "origin");
        formData.add("endName", "destination");
        return formData;
    }

    private String postAPIRequest(MultiValueMap<String, String> requestBody) {
        return webClient.post()
                .uri("/tmap/routes/pedestrian?version=1")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("appKey", apiKey)
                .header("Accept-Language", "ko")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private ObjectNode parseApiResponse(String apiResponse) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(apiResponse);

        JsonNode features = rootNode.path("features");
        JsonNode properties = features.get(0).path("properties");
        double totalDistance = properties.path("totalDistance").asDouble();
        int totalTime = properties.path("totalTime").asInt();

        ObjectNode result = mapper.createObjectNode();
        ArrayNode coordinatesArray = mapper.createArrayNode();

        for (JsonNode feature : features) {
            JsonNode geometry = feature.path("geometry");
            JsonNode coordinates = geometry.path("coordinates");

            if (geometry.path("type").asText().equals("Point")) {
                double latitude = coordinates.get(1).asDouble();
                double longitude = coordinates.get(0).asDouble();
                coordinatesArray.add(mapper.createObjectNode().put("latitude", latitude).put("longitude", longitude));
            } else if (geometry.path("type").asText().equals("LineString")) {
                for (JsonNode coordinate : coordinates) {
                    double latitude = coordinate.get(1).asDouble();
                    double longitude = coordinate.get(0).asDouble();
                    coordinatesArray.add(mapper.createObjectNode().put("latitude", latitude).put("longitude", longitude));
                }
            }
        }

        result.set("coordinates", coordinatesArray);
        result.put("totalTimeSeconds", totalTime);
        result.put("totalDistanceMeters", totalDistance);

        return result;
    }

    private ObjectNode getRoute(ObjectNode coordinates) throws IOException {

        String polyline = new PolylineEncoder().encode(coordinates.path("coordinates"));

        ObjectNode result = new ObjectMapper().createObjectNode();
        result.put("encodedPolyline", polyline);
        result.put("totalTimeSeconds", coordinates.path("totalTimeSeconds").asInt());
        result.put("totalDistanceMeters", coordinates.path("totalDistanceMeters").asDouble());

        return result;
    }

    private ArrayNode sortRoutesArrayByTime(ArrayNode routesArray) {
        List<JsonNode> list = new ArrayList<>();
        routesArray.forEach(list::add);

        Collections.sort(list, new Comparator<JsonNode>() {
            @Override
            public int compare(JsonNode o1, JsonNode o2) {
                return Integer.compare(o1.get("totalTimeSeconds").asInt(), o2.get("totalTimeSeconds").asInt());
            }
        });

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode sortedRoutesArray = mapper.createArrayNode();
        list.forEach(sortedRoutesArray::add);

        return sortedRoutesArray;
    }

    private void deduplicateRoutes(ArrayNode routesArray) {
        for (int i = 0; i < routesArray.size() - 1; i++) {
            JsonNode current = routesArray.get(i);
            JsonNode next = routesArray.get(i + 1);
            if (current.get("encodedPolyline").asText().equals(next.get("encodedPolyline").asText())) {
                routesArray.remove(i + 1);
                i--;
            }
        }
    }
}
