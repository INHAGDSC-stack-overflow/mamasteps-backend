package inhagdsc.mamasteps.map.service.regional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import inhagdsc.mamasteps.map.domain.LatLng;
import inhagdsc.mamasteps.map.domain.RouteEntity;
import inhagdsc.mamasteps.map.domain.RouteRequestProfileEntity;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GoogleApiService implements RegionalRouteApiService{
    private String googleApiKey;
    private String REQUEST_FIELDMASK;
    private boolean GET_POLYLINE_DIRECTLY;
    private final WebClient webClient;

    public GoogleApiService(Environment env, WebClient.Builder webClientBuilder) {
        this.googleApiKey = env.getProperty("GOOGLE_API_KEY");
        this.REQUEST_FIELDMASK = env.getProperty("REQUEST_FIELDMASK");
        this.GET_POLYLINE_DIRECTLY = Boolean.parseBoolean(env.getProperty("GET_POLYLINE_DIRECTLY"));
        this.webClient = webClientBuilder.baseUrl("https://routes.googleapis.com").build();
    }

    @Override
    public ObjectNode getParsedApiResponse(RouteRequestProfileEntity routeRequestEntity) throws IOException {
        return parseResponse(postApiRequest(routeRequestEntity));
    }

    private String buildRequestBody(RouteRequestProfileEntity routeRequestEntity) {
        JSONObject json = routeRequestEntity.toGoogleJson();
        json.put("travelMode", "WALK");
        json.put("routingPreference", "ROUTING_PREFERENCE_UNSPECIFIED");
        json.put("computeAlternativeRoutes", false);
        json.put("languageCode", "en-US");
        json.put("units", "METRIC");
        return json.toString();
    }

    private String postApiRequest(RouteRequestProfileEntity routeRequestEntity) {
        String requestBody = buildRequestBody(routeRequestEntity);

        return webClient.post()
                .uri("/directions/v2:computeRoutes")
                .header("Content-Type", "application/json")
                .header("X-Goog-Api-Key", googleApiKey)
                .header("X-Goog-FieldMask", REQUEST_FIELDMASK)
                .body(Mono.just(requestBody), String.class)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private ObjectNode parseResponse(String apiResponse) throws IOException {
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
}
