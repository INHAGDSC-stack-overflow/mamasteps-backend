package inhagdsc.mamasteps.map.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import inhagdsc.mamasteps.map.domain.LatLng;
import inhagdsc.mamasteps.map.domain.RouteRequestDto;
import inhagdsc.mamasteps.map.service.tool.PolylineEncoder;
import inhagdsc.mamasteps.map.service.tool.waypoint.ExploratoryWaypointGenerator;
import inhagdsc.mamasteps.map.service.tool.waypoint.WaypointGenerator;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@Service
@Profile("international")
public class InternationalRoutesService implements RoutesService {
    @Value("${GOOGLE_API_KEY}")
    private String apiKey;
    private final WebClient webClient;

    public InternationalRoutesService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://routes.googleapis.com").build();
    }

    @Override
    public String computeRoutes(RouteRequestDto originRouteRequestDto) throws RuntimeException, JsonProcessingException {
        WaypointGenerator waypointGenerator = new ExploratoryWaypointGenerator(originRouteRequestDto);
        List<LatLng> createdWaypoints = waypointGenerator.getSurroundingWaypoints();

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode result = mapper.createObjectNode();
        ArrayNode polylinesArray = mapper.createArrayNode();
        for (LatLng waypoint : createdWaypoints) {
            RouteRequestDto routeRequestDto = new RouteRequestDto();
            routeRequestDto.setTargetTime(originRouteRequestDto.getTargetTime());
            routeRequestDto.setOrigin(originRouteRequestDto.getOrigin().clone());
            routeRequestDto.setIntermediates(LatLng.deepCopyList(originRouteRequestDto.getIntermediates()));
            routeRequestDto.getIntermediates().add(waypoint);

            String requestBody = buildRequestBody(routeRequestDto);
            try {
                String polyline = encodePolyline(parseApiResponse(postAPIRequest(requestBody))).toString();
                polylinesArray.add(polyline);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        result.set("polylines", polylinesArray);
        return result.toString();

//        return postAPIRequest(requestBody)
//                .flatMap(response -> {
//                    try {
//                        ObjectNode parsedResponse = parseApiResponse(response);
//                        return Mono.just(encodePolyline(parsedResponse).toString());
//                    } catch (IOException e) {
//                        return Mono.error(new RuntimeException(e));
//                    }
//                });

    }

    private String buildRequestBody(RouteRequestDto routeRequestDto) {
        JSONObject json = routeRequestDto.toEntity().toGoogleJson();
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
                .header("X-Goog-FieldMask", "routes.duration,routes.distanceMeters,routes.legs")
                .body(Mono.just(requestBody), String.class)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private ObjectNode parseApiResponse(String apiResponse) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(apiResponse.toString());

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

    private ObjectNode encodePolyline(ObjectNode coordinates) throws IOException {

        String polyline =  new PolylineEncoder().encode(coordinates.path("coordinates"));

        ObjectNode result = new ObjectMapper().createObjectNode();
        result.put("encodedPolyline", polyline);
        result.put("totalTimeSeconds", coordinates.path("totalTimeSeconds").asInt());
        result.put("totalDistanceMeters", coordinates.path("totalDistanceMeters").asDouble());

        return result;
    }
}
