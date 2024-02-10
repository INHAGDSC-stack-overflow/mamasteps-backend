package inhagdsc.mamasteps.map.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import inhagdsc.mamasteps.map.domain.LatLng;
import inhagdsc.mamasteps.map.dto.RouteRequestDto;
import inhagdsc.mamasteps.map.domain.RouteRequestEntity;
import inhagdsc.mamasteps.map.domain.RoutesProfileEntity;
import inhagdsc.mamasteps.map.dto.RoutesProfileDto;
import inhagdsc.mamasteps.map.repository.RoutesProfileRepository;
import inhagdsc.mamasteps.map.service.tool.PolylineEncoder;
import inhagdsc.mamasteps.map.service.tool.waypoint.WaypointGenerator;
import inhagdsc.mamasteps.user.repository.UserRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Profile("international")
public class InternationalRoutesService implements RoutesService {
    @Value("${GOOGLE_API_KEY}")
    private String apiKey;
    @Value("${REQUEST_FIELDMASK}")
    private String REQUEST_FIELDMASK;
    @Value("${GET_POLYLINE_DIRECTLY}")
    private boolean GET_POLYLINE_DIRECTLY;
    private final WebClient webClient;
    private final WaypointGenerator waypointGenerator;
    private final RoutesProfileRepository routesProfileRepository;
    private final UserRepository userRepository;

    @Autowired
    public InternationalRoutesService(WebClient.Builder webClientBuilder, WaypointGenerator waypointGenerator, RoutesProfileRepository routesProfileRepository, UserRepository userRepository) {
        this.webClient = webClientBuilder.baseUrl("https://routes.googleapis.com").build();
        this.waypointGenerator = waypointGenerator;
        this.routesProfileRepository = routesProfileRepository;
        this.userRepository = userRepository;
    }

    @Override
    public RoutesProfileDto createProfile(Long userId, int currentNumber) {
        RoutesProfileEntity routesProfileEntity = new RoutesProfileEntity();
        routesProfileEntity.setUser(userRepository.findById(userId).get());
        routesProfileEntity.setProfileName("새 산책 프로필 " + (currentNumber + 1));
        routesProfileEntity.setStartCloseWaypoints(new ArrayList<>());
        routesProfileEntity.setEndCloseWaypoints(new ArrayList<>());
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        routesProfileEntity.setCreatedAt(now);
        routesProfileEntity.setUpdatedAt(now);

        routesProfileRepository.save(routesProfileEntity);
        return routesProfileEntity.toDto();
    }

    @Override
    public ObjectNode computeRoutes(RouteRequestDto routeRequestDto) throws RuntimeException, JsonProcessingException {
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

            String requestBody = buildRequestBody(routeRequestEntity);
            try {
                if (GET_POLYLINE_DIRECTLY) {
                    ObjectNode route = getRouteFromResponseDirectly(postAPIRequest(requestBody));
                    routesArray.add(route);
                }
                if (!GET_POLYLINE_DIRECTLY) {
                    ObjectNode route = getRoute(parseApiResponse(postAPIRequest(requestBody)));
                    routesArray.add(route);
                }
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

    private String buildRequestBody(RouteRequestEntity routeRequestEntity) {
        JSONObject json = routeRequestEntity.toGoogleJson();
        json.put("travelMode", "WALK");
        json.put("routingPreference", "ROUTING_PREFERENCE_UNSPECIFIED");
        json.put("computeAlternativeRoutes", false);
        json.put("languageCode", "en-US");
        json.put("units", "METRIC");
        return json.toString();
    }

    private String postAPIRequest(String requestBody) {
        return webClient.post()
                .uri("/directions/v2:computeRoutes")
                .header("Content-Type", "application/json")
                .header("X-Goog-Api-Key", apiKey)
                .header("X-Goog-FieldMask", REQUEST_FIELDMASK)
                .body(Mono.just(requestBody), String.class)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private ObjectNode getRouteFromResponseDirectly(String apiResponse) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(apiResponse);

        JsonNode route = rootNode.path("routes").get(0);
        double totalDistance = route.path("distanceMeters").asDouble();
        int totalTime = Integer.parseInt(route.path("duration").asText().replace("s", ""));
        String polyline = route.path("polyline").path("encodedPolyline").asText();

        ObjectNode result = mapper.createObjectNode();
        result.put("encodedPolyline", polyline);
        result.put("totalTimeSeconds", totalTime);
        result.put("totalDistanceMeters", totalDistance);

        return result;
    }

    private ObjectNode parseApiResponse(String apiResponse) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(apiResponse);

        JsonNode route = rootNode.path("routes").get(0);
        double totalDistance = route.path("distanceMeters").asDouble();
        int totalTime = Integer.parseInt(route.path("duration").asText().replace("s", ""));

        ObjectNode result = mapper.createObjectNode();
        ArrayNode coordinates = mapper.createArrayNode();

        JsonNode legs = route.path("legs");
        for (JsonNode leg : legs) {
            JsonNode steps = leg.path("steps");
            for (JsonNode step : steps) {
                JsonNode startLocation = step.path("startLocation").path("latLng");
                double startLatitude = startLocation.path("latitude").asDouble();
                double startLongitude = startLocation.path("longitude").asDouble();
                coordinates.add(mapper.createObjectNode().put("latitude", startLatitude).put("longitude", startLongitude));

                JsonNode endLocation = step.path("endLocation").path("latLng");
                double endLatitude = endLocation.path("latitude").asDouble();
                double endLongitude = endLocation.path("longitude").asDouble();
                coordinates.add(mapper.createObjectNode().put("latitude", endLatitude).put("longitude", endLongitude));
            }
        }

        result.set("coordinates", coordinates);
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
