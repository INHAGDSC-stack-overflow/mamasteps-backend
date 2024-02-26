package inhagdsc.mamasteps.map.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import inhagdsc.mamasteps.map.domain.*;
import inhagdsc.mamasteps.map.dto.requestProfile.EditRequestProfileRequest;
import inhagdsc.mamasteps.map.dto.requestProfile.EditRequestProfileResponse;
import inhagdsc.mamasteps.map.dto.requestProfile.GetRequestProfileResponse;
import inhagdsc.mamasteps.map.dto.route.ComputeRoutesResponse;
import inhagdsc.mamasteps.map.dto.route.SaveRouteRequest;
import inhagdsc.mamasteps.map.repository.ComputingRouteRepository;
import inhagdsc.mamasteps.map.repository.RouteRepository;
import inhagdsc.mamasteps.map.repository.RouteRequestProfileRepository;
import inhagdsc.mamasteps.map.service.regional.GoogleApiService;
import inhagdsc.mamasteps.map.service.regional.RegionalRouteApiService;
import inhagdsc.mamasteps.map.service.regional.TmapApiService;
import inhagdsc.mamasteps.map.service.tool.PolylineEncoder;
import inhagdsc.mamasteps.map.service.tool.WaypointGenerator;
import inhagdsc.mamasteps.user.dto.GetRoutesResponse;
import inhagdsc.mamasteps.user.entity.User;
import inhagdsc.mamasteps.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.RouteMatcher;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class RoutesServiceImpl implements RoutesService {
    @Value("${WAYPOINT_GENERATOR_NUMBER_OF_RESULTS}")
    private int NUMBER_OF_RESULTS;
    @Value("${DISTANCE_FACTOR_OPTIMIZE-NUMBER_OF_SAMPLES}")
    private int NUMBER_OF_SAMPLES;
    private final Environment env;
    private final WebClient.Builder webClientBuilder;
    private final WaypointGenerator waypointGenerator;
    private final RouteRepository routeRepository;
    private final ComputingRouteRepository computingRouteRepository;
    private final RouteRequestProfileRepository routeRequestProfileRepository;
    private final UserRepository userRepository;
    private final PolylineEncoder polylineEncoder;

    @Autowired
    public RoutesServiceImpl(Environment env, WebClient.Builder webClientBuilder, WaypointGenerator waypointGenerator, RouteRepository routeRepository, ComputingRouteRepository computingRouteRepository, RouteRequestProfileRepository routeRequestProfileRepository, UserRepository userRepository, PolylineEncoder polylineEncoder) {
        this.env = env;
        this.webClientBuilder = webClientBuilder;
        this.waypointGenerator = waypointGenerator;
        this.routeRepository = routeRepository;
        this.computingRouteRepository = computingRouteRepository;
        this.routeRequestProfileRepository = routeRequestProfileRepository;
        this.userRepository = userRepository;
        this.polylineEncoder = polylineEncoder;
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
            existingProfile.setDistanceFactor(1.0);
            existingProfile.setUpdatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            List<LatLng> createdWaypoints;
            try {
                createdWaypoints = waypointGenerator.getSurroundingWaypoints(existingProfile);
                existingProfile.setCreatedWaypointCandidates(createdWaypoints);
                optimizeDistanceFactor(existingProfile, NUMBER_OF_SAMPLES);
                createdWaypoints = waypointGenerator.getSurroundingWaypoints(existingProfile);
            } catch (IllegalArgumentException e) {
                createdWaypoints = new ArrayList<>(List.of(existingProfile.getOrigin()));
            }
            existingProfile.setCreatedWaypointCandidates(createdWaypoints);
        } else {
            // 새 프로필 생성
            existingProfile = new RouteRequestProfileEntity();
            existingProfile.setUser(user);
            existingProfile.setOrigin(user.getOrigin());
            existingProfile.setWalkSpeed(user.getWalkSpeed());
            existingProfile.setTargetTime(user.getTargetTime());
            existingProfile.setStartCloseWaypoints(new ArrayList<>());
            existingProfile.setEndCloseWaypoints(new ArrayList<>());
            existingProfile.setDistanceFactor(1.0);
            existingProfile.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            existingProfile.setUpdatedAt(existingProfile.getCreatedAt());

            List<LatLng> createdWaypoints;
            try {
                createdWaypoints = waypointGenerator.getSurroundingWaypoints(existingProfile);
                existingProfile.setCreatedWaypointCandidates(createdWaypoints);
                optimizeDistanceFactor(existingProfile, NUMBER_OF_SAMPLES);
                createdWaypoints = waypointGenerator.getSurroundingWaypoints(existingProfile);
            } catch (IllegalArgumentException e) {
                createdWaypoints = new ArrayList<>(List.of(existingProfile.getOrigin()));
            }
            existingProfile.setCreatedWaypointCandidates(createdWaypoints);
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
        requestProfileEntity.setDistanceFactor(1.0);
        requestProfileEntity.setUpdatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        List<LatLng> createdWaypoints;
        try {
            createdWaypoints = waypointGenerator.getSurroundingWaypoints(requestProfileEntity);
            requestProfileEntity.setCreatedWaypointCandidates(createdWaypoints);
            optimizeDistanceFactor(requestProfileEntity, NUMBER_OF_SAMPLES);
            createdWaypoints = waypointGenerator.getSurroundingWaypoints(requestProfileEntity);
        } catch (IllegalArgumentException e) {
            createdWaypoints = new ArrayList<>(List.of(requestProfileEntity.getOrigin()));
        }
        requestProfileEntity.setCreatedWaypointCandidates(createdWaypoints);

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
    public void saveRoute(User user, SaveRouteRequest request) {
        RouteEntity routeEntity = new RouteEntity();
        int routeCount = routeRepository.countByUserId(user.getId());
        routeEntity.setRouteName("내 산책 경로 " + (routeCount + 1));
        routeEntity.setUser(user);
        routeEntity.setPolyLine(request.getPolyLine());
        routeEntity.setTotalDistanceMeters(request.getTotalDistanceMeters());
        routeEntity.setTotalTimeSeconds(request.getTotalTimeSeconds());
        routeEntity.setCreatedAt(request.getCreatedAt());
        routeEntity.setUpdatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        routeRepository.save(routeEntity);
    }

    @Override
    public List<GetRoutesResponse> getRoutes(Long userId) {
        List<RouteEntity> routeEntities = routeRepository.findByUserId(userId);
        List<GetRoutesResponse> result = new ArrayList<>();
        for (RouteEntity routeEntity : routeEntities) {
            result.add(new GetRoutesResponse(routeEntity));
        }
        return result;
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
    @Transactional
    public List<ComputeRoutesResponse> computeRoutes(Long userId) throws IOException {
        List<ComputeRoutesResponse> result = new ArrayList<>();

        List<RouteEntity> computedRoutes = computingRouteRepository.findAllByUserId(userId);
        if (!computedRoutes.isEmpty()) {
            RouteEntity computedRoute = computedRoutes.get(0);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            if (!LocalDateTime.parse(computedRoute.getCreatedAt(), formatter).isBefore(LocalDateTime.now().minusSeconds(1))) {
                for (RouteEntity route : computedRoutes) {
                    result.add(new ComputeRoutesResponse(route));
                }
                return result;
            }
            computingRouteRepository.deleteAllByUserId(userId);
        }

        RouteRequestProfileEntity requestProfileEntity = routeRequestProfileRepository.findByUserId(userId).get();
        RegionalRouteApiService apiService = choiceApiService(requestProfileEntity.getOrigin());

        try {
            for (; result.size() < NUMBER_OF_RESULTS; ) {
                List<LatLng> waypointCandidates = LatLng.deepCopyList(requestProfileEntity.getCreatedWaypointCandidates());

                if (waypointCandidates.isEmpty()) {
                    throw new RuntimeException("No waypoints available");
                }

                List<LatLng> selectedWaypoints = selectRandomWaypoints(waypointCandidates, NUMBER_OF_RESULTS);
                for (LatLng waypoint : selectedWaypoints) {
                    RouteRequestProfileEntity copiedProfileEntity = new RouteRequestProfileEntity();
                    copiedProfileEntity.setTargetTime(requestProfileEntity.getTargetTime());
                    copiedProfileEntity.setOrigin(requestProfileEntity.getOrigin().clone());
                    copiedProfileEntity.setStartCloseWaypoints(LatLng.deepCopyList(requestProfileEntity.getStartCloseWaypoints()));
                    copiedProfileEntity.setEndCloseWaypoints(LatLng.deepCopyList(requestProfileEntity.getEndCloseWaypoints()));
                    copiedProfileEntity.getStartCloseWaypoints().add(waypoint);

                    RouteEntity route = buildRouteFromParsedResponse(waypoint, apiService.getParsedApiResponse(copiedProfileEntity));
                    route.setTotalTimeSeconds(getPersonalRequiredTime(route.getTotalDistanceMeters(), requestProfileEntity.getWalkSpeed()));

                    double differenceRatio = calculateDifferenceRatio(requestProfileEntity.getTargetTime(), route.getTotalTimeSeconds());
                    if (differenceRatio > 0.1) {
                        LatLng pulledWaypoint;
                        if (requestProfileEntity.getTargetTime() > route.getTotalTimeSeconds()) {
                            pulledWaypoint = pullWaypointToTarget(waypoint, requestProfileEntity.getOrigin(), differenceRatio + 1);
                        } else {
                            pulledWaypoint = pullWaypointToTarget(waypoint, requestProfileEntity.getOrigin(), 1 / (differenceRatio + 1));
                        }
                        copiedProfileEntity.getStartCloseWaypoints().remove(copiedProfileEntity.getStartCloseWaypoints().size() - 1);
                        copiedProfileEntity.getStartCloseWaypoints().add(pulledWaypoint);
                        route = buildRouteFromParsedResponse(pulledWaypoint, apiService.getParsedApiResponse(copiedProfileEntity));
                        route.setTotalTimeSeconds(getPersonalRequiredTime(route.getTotalDistanceMeters(), requestProfileEntity.getWalkSpeed()));
                        if (calculateDifferenceRatio(requestProfileEntity.getTargetTime(), route.getTotalTimeSeconds()) > 0.1) {
                            // try pull only once
                            requestProfileEntity.getCreatedWaypointCandidates().remove(waypoint);
                            continue;
                        }
                    }

                    requestProfileEntity.getCreatedWaypointCandidates().remove(waypoint);
                    result.add(new ComputeRoutesResponse(route));

                    route.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    User user = requestProfileEntity.getUser();
                    route.setUser(user);
                    computingRouteRepository.save(route);

                    if (result.size() >= NUMBER_OF_RESULTS) {
                        break;
                    }
                }
            }
        } catch (RuntimeException e) {
            result.clear();
        }

        routeRequestProfileRepository.save(requestProfileEntity);

        result.sort(Comparator.comparingInt(ComputeRoutesResponse::getTotalTimeSeconds));
        return result;
    }

    private void optimizeDistanceFactor(RouteRequestProfileEntity profile, int numberOfSamples) {
        RegionalRouteApiService apiService = choiceApiService(profile.getOrigin());

        List<LatLng> samples = new ArrayList<>();
        if (profile.getCreatedWaypointCandidates().size() <= numberOfSamples) {
            samples = new ArrayList<>(profile.getCreatedWaypointCandidates());
        }
        if (profile.getCreatedWaypointCandidates().size() > numberOfSamples) {
            samples = selectUniformWaypoints(profile.getCreatedWaypointCandidates(), numberOfSamples);
        }

        int sumOfTimeOfSamples = 0;
        for (LatLng sample : samples) {
            RouteRequestProfileEntity copiedProfileEntity = new RouteRequestProfileEntity();
            copiedProfileEntity.setTargetTime(profile.getTargetTime());
            copiedProfileEntity.setOrigin(profile.getOrigin().clone());
            copiedProfileEntity.setStartCloseWaypoints(LatLng.deepCopyList(profile.getStartCloseWaypoints()));
            copiedProfileEntity.setEndCloseWaypoints(LatLng.deepCopyList(profile.getEndCloseWaypoints()));
            copiedProfileEntity.getStartCloseWaypoints().add(sample);

            try {
                RouteEntity route = buildRouteFromParsedResponse(sample, apiService.getParsedApiResponse(copiedProfileEntity));
                route.setTotalTimeSeconds(getPersonalRequiredTime(route.getTotalDistanceMeters(), profile.getWalkSpeed()));
                sumOfTimeOfSamples += route.getTotalTimeSeconds();
            } catch (IOException e) {
                continue;
            }
        }
        double averageTimeOfSamples = (double) sumOfTimeOfSamples / samples.size();

        profile.setDistanceFactor(profile.getDistanceFactor() * (profile.getTargetTime() / averageTimeOfSamples));
    }

    private LatLng pullWaypointToTarget(LatLng waypoint, LatLng origin, double ratio) {
        double latitude = origin.getLatitude() + (waypoint.getLatitude() - origin.getLatitude()) * ratio;
        double longitude = origin.getLongitude() + (waypoint.getLongitude() - origin.getLongitude()) * ratio;
        return new LatLng(latitude, longitude);
    }

    private double calculateDifferenceRatio(double standard, double value) {
        return Math.abs(standard - value) / standard;
    }

    private List<LatLng> selectRandomWaypoints(List<LatLng> waypoints, int number) {
        if (waypoints.size() <= number) {
            return new ArrayList<>(waypoints);
        }

        List<LatLng> shuffledWaypoints = new ArrayList<>(waypoints);
        Collections.shuffle(shuffledWaypoints);

        List<LatLng> selectedWaypoints = new ArrayList<>(shuffledWaypoints.subList(0, number));
        return selectedWaypoints;
    }

    private List<LatLng> selectUniformWaypoints(List<LatLng> waypoints, int number) {
        if (waypoints.size() <= number) {
            return new ArrayList<>(waypoints);
        }

        List<LatLng> selectedWaypoints = new ArrayList<>();
        int interval = waypoints.size() / number;
        for (int i = 0; i < number; i++) {
            selectedWaypoints.add(waypoints.get(i * interval));
        }
        return selectedWaypoints;
    }

    private RegionalRouteApiService choiceApiService(LatLng origin) {
        if ((origin.getLatitude() > 33 && origin.getLatitude() < 39) && (origin.getLongitude() > 124 && origin.getLongitude() < 132)) {
            return new TmapApiService(env, webClientBuilder);
        } else {
            return new GoogleApiService(env, webClientBuilder);
        }
    }

    private RouteEntity buildRouteFromParsedResponse(LatLng createdWaypoint, ObjectNode coordinates) throws IOException {

        String polyline = polylineEncoder.encode(coordinates.path("coordinates"));
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

    private int getPersonalRequiredTime(double distance, double walkSpeed) {
        return (int) (distance / (walkSpeed / 3.6));
    }
}
