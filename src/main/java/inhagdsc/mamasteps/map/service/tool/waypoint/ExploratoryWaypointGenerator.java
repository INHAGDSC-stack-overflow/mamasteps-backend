package inhagdsc.mamasteps.map.service.tool.waypoint;

import inhagdsc.mamasteps.map.domain.LatLng;
import inhagdsc.mamasteps.map.domain.RouteRequestEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Profile("exploratory")
public class ExploratoryWaypointGenerator implements WaypointGenerator {
    @Value("${WAYPOINT_GENERATOR_AREA_DIVISION}")
    private int DIVISION;
    @Value("${DISTANCE_FACTOR}")
    private double DISTANCE_FACTOR;
    private int targetTime;
    private LatLng origin;
    private List<LatLng> startCloseIntermediates;
    private List<LatLng> endCloseIntermediates;

    public void setRouteRequestEntity(RouteRequestEntity routeRequestEntity) {
        this.targetTime = routeRequestEntity.getTargetTime();
        this.origin = routeRequestEntity.getOrigin();
        this.startCloseIntermediates = routeRequestEntity.getStartCloseIntermediates();
        this.endCloseIntermediates = routeRequestEntity.getEndCloseIntermediates();
    }

    public List<LatLng> getSurroundingWaypoints() {

        LatLng nextOfTarget;
        if (endCloseIntermediates.isEmpty()) {
            nextOfTarget = origin;
        }
        else {
            nextOfTarget = endCloseIntermediates.get(0);
        }

        LatLng previousOfTarget;
        if (startCloseIntermediates.isEmpty()) {
            previousOfTarget = origin;
        }
        else {
            previousOfTarget = startCloseIntermediates.get(startCloseIntermediates.size() - 1);
        }

        double smallerLat = Math.min(previousOfTarget.getLatitude(), nextOfTarget.getLatitude());
        double smallerLng = Math.min(previousOfTarget.getLongitude(), nextOfTarget.getLongitude());
        double largerLat = Math.max(previousOfTarget.getLatitude(), nextOfTarget.getLatitude());
        double largerLng = Math.max(previousOfTarget.getLongitude(), nextOfTarget.getLongitude());

        double requiredDistance = getRequiredDistance();
        double requiredDistanceInLatitude = requiredDistance / 111;
        double requiredDistanceInLongitude = (requiredDistance / 111) / Math.cos(Math.toRadians(previousOfTarget.getLatitude()));

        double rightEdge = smallerLng + requiredDistanceInLongitude;
        double leftEdge = largerLng - requiredDistanceInLongitude;
        double topEdge = smallerLat + requiredDistanceInLatitude;
        double bottomEdge = largerLat - requiredDistanceInLatitude;

        double width = rightEdge - leftEdge;
        double height = topEdge - bottomEdge;
        double horizDivUnit = width / DIVISION;
        double vertDivUnit = height / DIVISION;

        LatLng[][] square = new LatLng[DIVISION + 1][DIVISION + 1];
        for (int i = 0; i <= DIVISION; i++) {
            for (int j = 0; j <= DIVISION; j++) {
                square[i][j] = new LatLng(bottomEdge + horizDivUnit * i,
                        leftEdge + vertDivUnit * j);
            }
        }

        List<LatLng> surroundingWaypoints = new ArrayList<>();
        for (int i = 0; i <= DIVISION; i++) {
            for (int j = 0; j <= DIVISION; j++) {
                double distance = getDistanceBetweenThree(previousOfTarget, square[i][j], nextOfTarget);
                double longitude = square[i][j].getLongitude();
                double latitude = square[i][j].getLatitude();
                double tolerance = 2 * getDistance(longitude, latitude, longitude + horizDivUnit / 2, latitude + vertDivUnit / 2);
                if (distance < requiredDistance + tolerance && distance > requiredDistance - tolerance) {
                    surroundingWaypoints.add(square[i][j]);
                }
            }
        }

        return surroundingWaypoints;
    }

    private double getDistanceBetweenThree(LatLng first, LatLng middle, LatLng last) {
        return getDistance(
                first.getLongitude(),
                first.getLatitude(),
                middle.getLongitude(),
                middle.getLatitude()
        ) +
                getDistance(
                        middle.getLongitude(),
                        middle.getLatitude(),
                        last.getLongitude(),
                        last.getLatitude()
                );
    }

    private double getRequiredDistance() {
        List<LatLng> endCloseWaypoints = LatLng.deepCopyList(endCloseIntermediates);
        List<LatLng> startCloseWaypoints = LatLng.deepCopyList(startCloseIntermediates);
        endCloseWaypoints.add(origin);
        startCloseWaypoints.add(0, origin);

        double sumOfTerms = getDistanceBetweenLots(startCloseWaypoints) + getDistanceBetweenLots(endCloseWaypoints);

        return ((3.5 * targetTime / 60) - sumOfTerms) * DISTANCE_FACTOR;
    }

    private double getDistanceBetweenLots(List<LatLng> waypoints) {
        double sumOfTerms = 0;
        for (int i = 0; i < waypoints.size() - 1; i++) {
            LatLng waypoint1 = waypoints.get(i);
            LatLng waypoint2 = waypoints.get(i + 1);
            double x1 = waypoint1.getLongitude();
            double y1 = waypoint1.getLatitude();
            double x2 = waypoint2.getLongitude();
            double y2 = waypoint2.getLatitude();
            sumOfTerms += getDistance(x1, y1, x2, y2);
        }
        return sumOfTerms;
    }

    private double getDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(
                Math.pow(111 * (y2 - y1), 2) +
                        Math.pow(111 * (x2 - x1) * Math.cos(Math.toRadians(y1)), 2)
        );
    }
}
