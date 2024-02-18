package inhagdsc.mamasteps.map.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import inhagdsc.mamasteps.map.domain.*;
import inhagdsc.mamasteps.map.dto.*;
import inhagdsc.mamasteps.map.repository.RouteRepository;
import inhagdsc.mamasteps.map.repository.RouteRequestProfileRepository;
import inhagdsc.mamasteps.map.service.tool.PolylineEncoder;
import inhagdsc.mamasteps.map.service.tool.waypoint.WaypointGenerator;
import inhagdsc.mamasteps.user.entity.User;
import inhagdsc.mamasteps.user.repository.UserRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Value("${WAYPOINT_GENERATOR_NUMBER_OF_RESULTS}")
    private int NUMBER_OF_RESULTS;

    private final WebClient googleWebClient;
    private final WebClient tmapWebClient;
    private final WaypointGenerator waypointGenerator;
    private final RouteRepository routeRepository;
    private final RouteRequestProfileRepository routeRequestProfileRepository;
    private final UserRepository userRepository;

    @Autowired
    public RoutesServiceImpl(WebClient.Builder webClientBuilder, WaypointGenerator waypointGenerator, RouteRepository routeRepository, RouteRequestProfileRepository routeRequestProfileRepository, UserRepository userRepository) {
        this.googleWebClient = webClientBuilder.baseUrl("https://routes.googleapis.com").build();
        this.tmapWebClient = webClientBuilder.baseUrl("https://apis.openapi.sk.com").build();
        this.waypointGenerator = waypointGenerator;
        this.routeRepository = routeRepository;
        this.routeRequestProfileRepository = routeRequestProfileRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void createRequestProfile(User user) {
        Optional<RouteRequestProfileEntity> existingProfileOptional = routeRequestProfileRepository.findByUserId(user.getId());
        RouteRequestProfileEntity existingProfile;
        if (user.getOrigin() == null) {
            throw new IllegalArgumentException("no origin in user");
        }
        if (!existingProfileOptional.isEmpty()) {
            existingProfile = existingProfileOptional.get();
            // 기존 프로필 업데이트
            existingProfile.setOrigin(user.getOrigin());
            existingProfile.setWalkSpeed(user.getWalkSpeed());
            existingProfile.setTargetTime(user.getTargetTime());
            existingProfile.setStartCloseWaypoints(new ArrayList<>());
            existingProfile.setEndCloseWaypoints(new ArrayList<>());
            List<LatLng> createdWaypoints = waypointGenerator.getSurroundingWaypoints(existingProfile);
            existingProfile.setCreatedWaypointCandidate(createdWaypoints);
            existingProfile.setUpdatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        } else {
            // 새 프로필 생성
            existingProfile = new RouteRequestProfileEntity();
            existingProfile.setUser(user);
            existingProfile.setOrigin(user.getOrigin());
            existingProfile.setWalkSpeed(user.getWalkSpeed());
            existingProfile.setTargetTime(user.getTargetTime());
            existingProfile.setStartCloseWaypoints(new ArrayList<>());
            existingProfile.setEndCloseWaypoints(new ArrayList<>());
            List<LatLng> createdWaypoints = waypointGenerator.getSurroundingWaypoints(existingProfile);
            existingProfile.setCreatedWaypointCandidate(createdWaypoints);
            existingProfile.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            existingProfile.setUpdatedAt(existingProfile.getCreatedAt());
        }

        routeRequestProfileRepository.save(existingProfile);
    }

    @Override
    @Transactional
    public EditRequestProfileResponse editRequestProfile(Long userId, EditRequestProfileRequest request) {
        RouteRequestProfileEntity requestProfileEntity = routeRequestProfileRepository.findByUserId(userId).get();
        if (request.getOrigin() == null) {
            throw new IllegalArgumentException("no origin in request");
        }
        requestProfileEntity.setOrigin(request.getOrigin());
        requestProfileEntity.setTargetTime(request.getTargetTime());
        requestProfileEntity.setWalkSpeed(request.getWalkSpeed());
        requestProfileEntity.setStartCloseWaypoints(request.getStartCloseWaypoints());
        requestProfileEntity.setEndCloseWaypoints(request.getEndCloseWaypoints());
        List<LatLng> createdWaypoints = waypointGenerator.getSurroundingWaypoints(requestProfileEntity);
        requestProfileEntity.setCreatedWaypointCandidate(createdWaypoints);
        requestProfileEntity.setUpdatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        routeRequestProfileRepository.save(requestProfileEntity);
        return new EditRequestProfileResponse(requestProfileEntity);
    }

    @Override
    public GetRequestProfileResponse getRequestProfile(Long userId) {
        Optional<RouteRequestProfileEntity> requestProfileEntityOptional = routeRequestProfileRepository.findByUserId(userId);
        if (requestProfileEntityOptional.isEmpty()) {
            throw new IllegalArgumentException("사용자에게 프로필이 없습니다.");
        }
        return new GetRequestProfileResponse(requestProfileEntityOptional.get());
    }

    @Override
    @Transactional
    public void saveRoute(Long userId, RouteDto routeDto) {
        RouteEntity routeEntity = new RouteEntity();
        int routeCount = routeRepository.countByUserId(userId);
        routeEntity.setRouteName("내 산책 경로 " + (routeCount + 1));
        routeEntity.setUserId(userId);
        routeEntity.setWalkSpeed(routeDto.getWalkSpeed());
        routeEntity.setCreatedWaypoint(routeDto.getCreatedWaypoint());
        routeEntity.setPolyLine(routeDto.getPolyLine());
        routeEntity.setTotalDistanceMeters(routeDto.getTotalDistanceMeters());
        routeEntity.setTotalTimeSeconds(routeDto.getTotalTimeSeconds());
        routeEntity.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        routeEntity.setUpdatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        routeRepository.save(routeEntity);
    }

    @Override
    public List<RouteDto> getRoutes(Long userId) {
        List<RouteEntity> routeEntities = routeRepository.findByUserId(userId);
        return RouteEntity.toDtoList(routeEntities);
    }

    @Override
    public void editRouteName(Long userId, Long routeId, String name) {
        Optional<RouteEntity> routeEntityOptional = routeRepository.findByIdAndUserId(routeId, userId);

        if (routeEntityOptional.isPresent()) {
            RouteEntity routeEntity = routeEntityOptional.get();
            routeEntity.setRouteName(name);
            routeEntity.setUpdatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            routeRepository.save(routeEntity);
        } else {
            throw new IllegalArgumentException("사용자에게 해당 루트가 없습니다.");
        }
    }

    @Override
    @Transactional
    public void deleteRoute(Long userId, Long routeId) {
        Optional<RouteEntity> routeEntityOptional = routeRepository.findByIdAndUserId(routeId, userId);

        if (routeEntityOptional.isPresent()) {
            routeRepository.deleteById(routeId);
        } else {
            throw new IllegalArgumentException("사용자에게 해당 루트가 없습니다.");
        }
    }

    @Override
    public List<ComputeRoutesResponse> computeRoutes(Long userId) throws IOException {

        RouteRequestProfileEntity requestProfileEntity = routeRequestProfileRepository.findByUserId(userId).get();

        boolean timeOverflow = false;
        List<LatLng> createdWaypoints = new ArrayList<>(requestProfileEntity.getCreatedWaypointCandidate());
        if (createdWaypoints.isEmpty()) {
            timeOverflow = true;
            createdWaypoints.add(requestProfileEntity.getOrigin());
        }

        List<LatLng> selectedWaypoints = selectWaypoints(createdWaypoints, NUMBER_OF_RESULTS);
        if (selectedWaypoints.isEmpty()) {
            throw new RuntimeException("No waypoints available");
        }

        double originLatitude = requestProfileEntity.getOrigin().getLatitude();
        double originLongitude = requestProfileEntity.getOrigin().getLongitude();
        List<RouteEntity> routesList;
        if ((originLatitude > 33 && originLatitude < 39) && (originLongitude > 124 && originLongitude < 132)) {
            routesList = computeRoutesUsingTmap(requestProfileEntity, selectedWaypoints, timeOverflow);
        } else {
            routesList = computeRoutesUsingGoogle(requestProfileEntity, selectedWaypoints, timeOverflow);
        }

        List<ComputeRoutesResponse> result = new ArrayList<>();
        for (RouteEntity route : routesList) {
            result.add(new ComputeRoutesResponse(route));
        }

        result.sort(Comparator.comparingInt(ComputeRoutesResponse::getTotalTimeSeconds));
        return result;
    }

    private List<RouteEntity> computeRoutesUsingGoogle(RouteRequestProfileEntity requestProfileEntity, List<LatLng> selectedWaypoints, boolean timeOverflow) throws IOException {
        List<RouteEntity> result = new ArrayList<>();
        for (LatLng waypoint : selectedWaypoints) {
            RouteRequestProfileEntity routeRequestEntity = new RouteRequestProfileEntity();
            routeRequestEntity.setTargetTime(requestProfileEntity.getTargetTime());
            routeRequestEntity.setOrigin(requestProfileEntity.getOrigin().clone());
            routeRequestEntity.setStartCloseWaypoints(LatLng.deepCopyList(requestProfileEntity.getStartCloseWaypoints()));
            routeRequestEntity.setEndCloseWaypoints(LatLng.deepCopyList(requestProfileEntity.getEndCloseWaypoints()));
            if (!timeOverflow) {
                routeRequestEntity.getStartCloseWaypoints().add(waypoint);
            }

            String requestBody = buildGoogleRequestBody(routeRequestEntity);
            RouteEntity route;
            try {
                if (GET_POLYLINE_DIRECTLY) {
                    route = getRouteFromResponseDirectly(waypoint, postGoogleAPIRequest(requestBody));
                }
                else {
                    route = getRoute(waypoint, parseGoogleApiResponse(postGoogleAPIRequest(requestBody)));
                }
                route.setTotalTimeSeconds(getPersonalRequiredTime(route.getTotalDistanceMeters(), requestProfileEntity.getWalkSpeed()));
                result.add(route);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return result;
    }

    private List<RouteEntity> computeRoutesUsingTmap(RouteRequestProfileEntity requestProfileEntity, List<LatLng> selectedWaypoints, boolean timeOverflow) throws IOException {
        List<RouteEntity> result = new ArrayList<>();
        for (LatLng waypoint : selectedWaypoints) {
            RouteRequestProfileEntity routeRequestEntity = new RouteRequestProfileEntity();
            routeRequestEntity.setTargetTime(requestProfileEntity.getTargetTime());
            routeRequestEntity.setOrigin(requestProfileEntity.getOrigin().clone());
            routeRequestEntity.setStartCloseWaypoints(LatLng.deepCopyList(requestProfileEntity.getStartCloseWaypoints()));
            routeRequestEntity.setEndCloseWaypoints(LatLng.deepCopyList(requestProfileEntity.getEndCloseWaypoints()));
            if (!timeOverflow) {
                routeRequestEntity.getStartCloseWaypoints().add(waypoint);
            }

            MultiValueMap<String, String> requestBody = buildTmapRequestBody(routeRequestEntity);
            try {
                RouteEntity route = getRoute(waypoint, parseTmapApiResponse(postTmapAPIRequest(requestBody)));
                route.setTotalTimeSeconds(getPersonalRequiredTime(route.getTotalDistanceMeters(), requestProfileEntity.getWalkSpeed()));
                result.add(route);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return result;
    }

    private List<LatLng> selectWaypoints(List<LatLng> waypoints, int number) {
        // 입력 리스트의 크기가 요청한 number보다 작거나 같으면 전체 리스트 반환
        if (waypoints.size() <= number) {
            return new ArrayList<>(waypoints);
        }

        List<LatLng> shuffledWaypoints = new ArrayList<>(waypoints);
        // waypoints 리스트를 무작위로 섞음
        Collections.shuffle(shuffledWaypoints);

        // 섞인 리스트에서 상위 number 개의 요소를 선택
        List<LatLng> selectedWaypoints = new ArrayList<>(shuffledWaypoints.subList(0, number));

        //        if (waypoints.size() <= number) {
//            selectedWaypoints = waypoints;
//        }
//        else {
//            for (int i = 0; i < waypoints.size(); i++) {
//                if (i % (waypoints.size() / number) == 0) {
//                    selectedWaypoints.add(waypoints.get(i));
//                }
//            }
//        }

        return selectedWaypoints;
    }

    private String buildGoogleRequestBody(RouteRequestProfileEntity routeRequestEntity) {
        JSONObject json = routeRequestEntity.toGoogleJson();
        json.put("travelMode", "WALK");
        json.put("routingPreference", "ROUTING_PREFERENCE_UNSPECIFIED");
        json.put("computeAlternativeRoutes", false);
        json.put("languageCode", "en-US");
        json.put("units", "METRIC");
        return json.toString();
    }

    private MultiValueMap<String, String> buildTmapRequestBody(RouteRequestProfileEntity routeRequestEntity) {
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

    private RouteEntity getRoute(LatLng createdWaypoint, ObjectNode coordinates) throws IOException {

        String polyline = new PolylineEncoder().encode(coordinates.path("coordinates"));
        int totalTimeSeconds = coordinates.path("totalTimeSeconds").asInt();
        double totalDistanceMeters = coordinates.path("totalDistanceMeters").asDouble();

        RouteEntity result = new RouteEntity();
        result.setCreatedWaypoint(createdWaypoint);
        result.setPolyLine(polyline);
        result.setTotalDistanceMeters(totalDistanceMeters);
        result.setTotalTimeSeconds(totalTimeSeconds);
        result.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        result.setUpdatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return result;
    }

    private RouteEntity getRouteFromResponseDirectly(LatLng createdWaypoint, String apiResponse) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(apiResponse);

        JsonNode route = rootNode.path("routes").get(0);
        double totalDistanceMeters = route.path("distanceMeters").asDouble();
        int totalTimeSeconds = Integer.parseInt(route.path("duration").asText().replace("s", ""));
        String polyline = route.path("polyline").path("encodedPolyline").asText();

        RouteEntity result = new RouteEntity();
        result.setCreatedWaypoint(createdWaypoint);
        result.setPolyLine(polyline);
        result.setTotalDistanceMeters(totalDistanceMeters);
        result.setTotalTimeSeconds(totalTimeSeconds);
        result.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        result.setUpdatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return result;
    }

    private int getPersonalRequiredTime(double distance, double walkSpeed) {
        return (int)(distance / (walkSpeed / 3.6));
    }

    private List<RouteEntity> sortRoutesArrayByTime(List<RouteEntity> routesList) {
        List<RouteEntity> list = new ArrayList<>(routesList);

        list.sort(Comparator.comparingInt(RouteEntity::getTotalTimeSeconds));

        return list;
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
