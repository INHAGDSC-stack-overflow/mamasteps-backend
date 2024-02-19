package inhagdsc.mamasteps.map.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import inhagdsc.mamasteps.map.domain.*;
import inhagdsc.mamasteps.map.dto.requestProfile.EditRequestProfileRequest;
import inhagdsc.mamasteps.map.dto.requestProfile.EditRequestProfileResponse;
import inhagdsc.mamasteps.map.dto.requestProfile.GetRequestProfileResponse;
import inhagdsc.mamasteps.map.dto.route.ComputeRoutesResponse;
import inhagdsc.mamasteps.map.dto.route.SaveRouteRequest;
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
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class RoutesServiceImpl implements RoutesService {
    @Value("${WAYPOINT_GENERATOR_NUMBER_OF_RESULTS}")
    private int NUMBER_OF_RESULTS;
    private final Environment env;
    private final WebClient.Builder webClientBuilder;
    private final WaypointGenerator waypointGenerator;
    private final RouteRepository routeRepository;
    private final RouteRequestProfileRepository routeRequestProfileRepository;
    private final UserRepository userRepository;

    @Autowired
    public RoutesServiceImpl(Environment env, WebClient.Builder webClientBuilder, WaypointGenerator waypointGenerator, RouteRepository routeRepository, RouteRequestProfileRepository routeRequestProfileRepository, UserRepository userRepository) {
        this.env = env;
        this.webClientBuilder = webClientBuilder;
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
    public void saveRoute(Long userId, SaveRouteRequest request) {
        RouteEntity routeEntity = new RouteEntity();
        int routeCount = routeRepository.countByUserId(userId);
        routeEntity.setRouteName("내 산책 경로 " + (routeCount + 1));
        routeEntity.setUserId(userId);
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
    public List<ComputeRoutesResponse> computeRoutes(Long userId) throws IOException {
        RouteRequestProfileEntity requestProfileEntity = routeRequestProfileRepository.findByUserId(userId).get();
        List<LatLng> waypointCandidates = LatLng.deepCopyList(requestProfileEntity.getCreatedWaypointCandidates());

        boolean timeOverflow = false;
        if (waypointCandidates.isEmpty()) {
            timeOverflow = true;
            waypointCandidates.add(requestProfileEntity.getOrigin());
        }

        List<LatLng> selectedWaypoints = selectWaypoints(waypointCandidates, NUMBER_OF_RESULTS);
        RegionalRouteApiService apiService = choiceApiService(requestProfileEntity.getOrigin());

        List<ComputeRoutesResponse> result = new ArrayList<>();
        for (LatLng waypoint : selectedWaypoints) {
            RouteRequestProfileEntity copiedProfileEntity = new RouteRequestProfileEntity();
            copiedProfileEntity.setTargetTime(requestProfileEntity.getTargetTime());
            copiedProfileEntity.setOrigin(requestProfileEntity.getOrigin().clone());
            copiedProfileEntity.setStartCloseWaypoints(LatLng.deepCopyList(requestProfileEntity.getStartCloseWaypoints()));
            copiedProfileEntity.setEndCloseWaypoints(LatLng.deepCopyList(requestProfileEntity.getEndCloseWaypoints()));
            if (!timeOverflow) {
                copiedProfileEntity.getStartCloseWaypoints().add(waypoint);
            }

            RouteEntity route = buildRouteFromParsedResponse(waypoint, apiService.getParsedApiResponse(copiedProfileEntity));
            route.setTotalTimeSeconds(getPersonalRequiredTime(route.getTotalDistanceMeters(), requestProfileEntity.getWalkSpeed()));
            result.add(new ComputeRoutesResponse(route));
        }

        result.sort(Comparator.comparingInt(ComputeRoutesResponse::getTotalTimeSeconds));
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
        if (selectedWaypoints.isEmpty()) {
            throw new RuntimeException("No waypoints available");
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

    private int getPersonalRequiredTime(double distance, double walkSpeed) {
        return (int)(distance / (walkSpeed / 3.6));
    }
}
