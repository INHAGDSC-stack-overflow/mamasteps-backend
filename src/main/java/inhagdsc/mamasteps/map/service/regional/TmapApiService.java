package inhagdsc.mamasteps.map.service.regional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import inhagdsc.mamasteps.map.domain.LatLng;
import inhagdsc.mamasteps.map.domain.RouteRequestProfileEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TmapApiService implements RegionalRouteApiService{
    @Value("${TMAP_API_KEY}")
    private String tmapApiKey;

    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(GoogleApiService.class);
    private static long requestCount = 0;

    public TmapApiService(Environment env, WebClient.Builder webClientBuilder) {
        this.tmapApiKey = env.getProperty("TMAP_API_KEY");
        this.webClient = webClientBuilder.baseUrl("https://apis.openapi.sk.com").build();
    }

    @Override
    public ObjectNode getParsedApiResponse(RouteRequestProfileEntity routeRequestEntity) throws IOException {
        return parseResponse(postApiRequest(routeRequestEntity));
    }

    private MultiValueMap<String, String> buildRequestBody(RouteRequestProfileEntity routeRequestEntity) {
        MultiValueMap<String, String> formData = buildTmapValueMap(routeRequestEntity);
        formData.add("speed", "35");
        formData.add("startName", "origin");
        formData.add("endName", "destination");
        return formData;
    }

    private MultiValueMap<String, String> buildTmapValueMap(RouteRequestProfileEntity routeRequestEntity) {
        MultiValueMap<String, String> valueMap = new LinkedMultiValueMap<>();
        valueMap.add("startX", Double.toString(routeRequestEntity.getOrigin().getLongitude()));
        valueMap.add("startY", Double.toString(routeRequestEntity.getOrigin().getLatitude()));
        valueMap.add("endX", Double.toString(routeRequestEntity.getOrigin().getLongitude()));
        valueMap.add("endY", Double.toString(routeRequestEntity.getOrigin().getLatitude()));

        List<String> passList = new ArrayList<>();
        for (LatLng latLng : routeRequestEntity.getStartCloseWaypoints()) {
            passList.add(Double.toString(latLng.getLongitude()) + "," + Double.toString(latLng.getLatitude()));
        }
        for (LatLng latLng : routeRequestEntity.getEndCloseWaypoints()) {
            passList.add(Double.toString(latLng.getLongitude()) + "," + Double.toString(latLng.getLatitude()));
        }
        valueMap.add("passList", passList.stream().collect(Collectors.joining("_")));

        return valueMap;
    }

    private String postApiRequest(RouteRequestProfileEntity routeRequestEntity) {
        MultiValueMap<String, String> requestBody = buildRequestBody(routeRequestEntity);

        requestCount++;
        logger.info("TmapApiRequest has been called {} times.", requestCount);

        return webClient.post()
                .uri("/tmap/routes/pedestrian?version=1")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("appKey", tmapApiKey)
                .header("Accept-Language", "ko")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private ObjectNode parseResponse(String apiResponse) throws IOException {
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
}
