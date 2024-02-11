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
import jakarta.persistence.EntityNotFoundException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class RoutesServiceImpl implements RoutesService {
    @Value("${GOOGLE_API_KEY}")
    private String googleApiKey;
    @Value("${REQUEST_FIELDMASK}")
    private String REQUEST_FIELDMASK;
    @Value("${GET_POLYLINE_DIRECTLY}")
    private boolean GET_POLYLINE_DIRECTLY;

    @Value("${TMAP_API_KEY}")
    private String tmapApiKey;

    private final WebClient googleWebClient;
    private final WebClient tmapWebClient;
    private final WaypointGenerator waypointGenerator;
    private final RoutesProfileRepository routesProfileRepository;
    private final UserRepository userRepository;

    @Autowired
    public RoutesServiceImpl(WebClient.Builder webClientBuilder, WaypointGenerator waypointGenerator, RoutesProfileRepository routesProfileRepository, UserRepository userRepository) {
        this.googleWebClient = webClientBuilder.baseUrl("https://routes.googleapis.com").build();
        this.tmapWebClient = webClientBuilder.baseUrl("https://apis.openapi.sk.com").build();
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
    public List<RoutesProfileDto> getProfiles(Long userId) {
        List<RoutesProfileEntity> routesProfileEntities = routesProfileRepository.findAllByUserId(userId);

        List<RoutesProfileDto> routesProfileDtos = new ArrayList<>();
        if (routesProfileEntities.isEmpty()) {
            routesProfileDtos.add(createProfile(userId, 0));
        }
        for (RoutesProfileEntity entity : routesProfileEntities) {
            routesProfileDtos.add(entity.toDto());
        }

        return routesProfileDtos;
    }

    @Override
    public void editProfile(Long userId, Long profileId, RoutesProfileDto routesProfileDto) {
        RoutesProfileEntity profile = routesProfileRepository.findAllByUserId(userId).stream()
                .filter(routesProfileEntity -> routesProfileEntity.getId().equals(profileId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Profile with ID " + profileId + " not found for User ID " + userId));
        profile.setProfileName(routesProfileDto.getProfileName());
        profile.setTargetTime(routesProfileDto.getTargetTime());
        profile.setOrigin(routesProfileDto.getOrigin());
        profile.setStartCloseWaypoints(routesProfileDto.getStartCloseWaypoints());
        profile.setEndCloseWaypoints(routesProfileDto.getEndCloseWaypoints());
        profile.setUpdatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        routesProfileRepository.save(profile);
    }

    @Override
    public void deleteProfile(Long userId, Long profileId) {
        RoutesProfileEntity profile = routesProfileRepository.findAllByUserId(userId).stream()
                .filter(routesProfileEntity -> routesProfileEntity.getId().equals(profileId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Profile with ID " + profileId + " not found for User ID " + userId));
        routesProfileRepository.delete(profile);
    }

    @Override
    public ObjectNode computeRoutes(RouteRequestDto routeRequestDto) throws IOException {
        RouteRequestEntity originRouteRequestEntity = routeRequestDto.toEntity();
        waypointGenerator.setRouteRequestEntity(originRouteRequestEntity);

        double originLatitude = originRouteRequestEntity.getOrigin().getLatitude();
        double originLongitude = originRouteRequestEntity.getOrigin().getLongitude();
        if ((originLatitude > 33 && originLatitude < 39) && (originLongitude > 124 && originLongitude < 132)) {
            return computeRoutesUsingTmap(originRouteRequestEntity);
        } else {
            return computeRoutesUsingGoogle(originRouteRequestEntity);
        }

    }

    private ObjectNode computeRoutesUsingGoogle(RouteRequestEntity originRouteRequestEntity) throws IOException {
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

            String requestBody = buildGoogleRequestBody(routeRequestEntity);
            try {
                if (GET_POLYLINE_DIRECTLY) {
                    ObjectNode route = getRouteFromResponseDirectly(postGoogleAPIRequest(requestBody));
                    routesArray.add(route);
                }
                if (!GET_POLYLINE_DIRECTLY) {
                    ObjectNode route = getRoute(parseGoogleApiResponse(postGoogleAPIRequest(requestBody)));
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

    private ObjectNode computeRoutesUsingTmap(RouteRequestEntity originRouteRequestEntity) throws IOException {
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

            MultiValueMap<String, String> requestBody = buildTmapRequestBody(routeRequestEntity);
            try {
                ObjectNode route = getRoute(parseTmapApiResponse(postTmapAPIRequest(requestBody)));
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

    private String buildGoogleRequestBody(RouteRequestEntity routeRequestEntity) {
        JSONObject json = routeRequestEntity.toGoogleJson();
        json.put("travelMode", "WALK");
        json.put("routingPreference", "ROUTING_PREFERENCE_UNSPECIFIED");
        json.put("computeAlternativeRoutes", false);
        json.put("languageCode", "en-US");
        json.put("units", "METRIC");
        return json.toString();
    }

    private MultiValueMap<String, String> buildTmapRequestBody(RouteRequestEntity routeRequestEntity) {
        MultiValueMap<String, String> formData = routeRequestEntity.toTmapValueMap();
        formData.add("speed", "35");
        formData.add("startName", "origin");
        formData.add("endName", "destination");
        return formData;
    }

    private String postGoogleAPIRequest(String requestBody) {
        return googleWebClient.post()
                .uri("/directions/v2:computeRoutes")
                .header("Content-Type", "application/json")
                .header("X-Goog-Api-Key", googleApiKey)
                .header("X-Goog-FieldMask", REQUEST_FIELDMASK)
                .body(Mono.just(requestBody), String.class)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private String postTmapAPIRequest(MultiValueMap<String, String> requestBody) {
        return tmapWebClient.post()
                .uri("/tmap/routes/pedestrian?version=1")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("appKey", tmapApiKey)
                .header("Accept-Language", "ko")
                .bodyValue(requestBody)
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

    private ObjectNode parseGoogleApiResponse(String apiResponse) throws IOException {
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

    private ObjectNode parseTmapApiResponse(String apiResponse) throws JsonProcessingException {
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
