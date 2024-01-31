package inhagdsc.mamasteps.map.service.tool.waypoint;

import inhagdsc.mamasteps.map.domain.LatLng;
import inhagdsc.mamasteps.map.domain.RouteRequestEntity;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Profile("mathematical")
public class MathematicalWaypointGenerator implements WaypointGenerator {
    private int targetTime;
    private LatLng origin;
    private List<LatLng> intermediates;

    public void setRouteRequestEntity(RouteRequestEntity routeRequestEntity) {
        this.targetTime = routeRequestEntity.getTargetTime();
        this.origin = routeRequestEntity.getOrigin();
        this.intermediates = routeRequestEntity.getIntermediates();
    }

    public List<LatLng> getSurroundingWaypoints() {
        List<LatLng> halfWaypoints = getHalfWaypoints();
        List<LatLng> wholeWaypoints = getWholeWaypoints(halfWaypoints);
        return wholeWaypoints;
    }

    private List<LatLng> getWholeWaypoints(List<LatLng> halfWaypoints) {
        List<LatLng> wholeWaypoints = new ArrayList<>(halfWaypoints);

        LatLng lastWaypoint = halfWaypoints.get(halfWaypoints.size() - 1);
        LatLng center = new LatLng((origin.getLatitude() + lastWaypoint.getLatitude()) / 2,
                (origin.getLongitude() + lastWaypoint.getLongitude()) / 2);

        for (LatLng point : halfWaypoints) {
            double mirroredLat = 2 * center.getLatitude() - point.getLatitude();
            double mirroredLng = 2 * center.getLongitude() - point.getLongitude();
            wholeWaypoints.add(new LatLng(mirroredLat, mirroredLng));
        }

        return wholeWaypoints;
    }

    private List<LatLng> getHalfWaypoints() {
        double minY = getYFromX(getMinX());
        double maxY = getMaxYFromMinY(minY);

        double divUnit = (maxY - minY) / 8;

        List<Double> dividingYValues = new ArrayList<>();
        for (double y = minY; y <= maxY; y += divUnit) {
            dividingYValues.add(y);
        }

        List<LatLng> halfWaypoints = new ArrayList<>();
        for (double y : dividingYValues) {
            double x = getXFromY(y);
            halfWaypoints.add(new LatLng(y, x));
        }

        return halfWaypoints;
    }


    private double getMaxYFromMinY(double minY) {
        double biggerFocus;
        double smallerFocus;
        LatLng lastWaypoint = intermediates.get(intermediates.size() - 1);

        if (origin.getLatitude() > lastWaypoint.getLatitude()) {
            biggerFocus = origin.getLatitude();
            smallerFocus = lastWaypoint.getLatitude();
        } else {
            biggerFocus = lastWaypoint.getLatitude();
            smallerFocus = origin.getLatitude();
        }

        return biggerFocus + (smallerFocus - minY);
    }

    private double getXFromY(double y) {
        BrentSolver solver = new BrentSolver(1e-6);
        double x = solver.solve(100, ellipseReturnsX(y), -100, 100);
        return x;
    }

    private UnivariateFunction ellipseReturnsX(double nextY) {
        return x -> {
            List<LatLng> waypoints = LatLng.deepCopyList(intermediates);
            waypoints.add(0, origin);
            waypoints.add(origin);
            double sumOfTerms = 0;
            for (int i = 0; i < waypoints.size() - 1; i++) {
                LatLng waypoint1 = waypoints.get(i);
                LatLng waypoint2 = waypoints.get(i + 1);
                double x1 = waypoint1.getLongitude();
                double y1 = waypoint1.getLatitude();
                double x2 = waypoint2.getLongitude();
                double y2 = waypoint2.getLatitude();
                sumOfTerms += calculateTerm(x1, y1, x2, y2);
            }

            sumOfTerms += calculateTerm(x, nextY, origin.getLongitude(), origin.getLatitude());

            return sumOfTerms - 4.6 * targetTime;
        };
    }

    private double getYFromX(double x) {
        BrentSolver solver = new BrentSolver(1e-6);
        double y = solver.solve(100, ellipseReturnsY(x), -100, 100);
        return y;
    }

    private UnivariateFunction ellipseReturnsY(double nextX) {
        return y -> {
            List<LatLng> waypoints = LatLng.deepCopyList(intermediates);
            waypoints.add(0, origin);
            waypoints.add(origin);
            double sumOfTerms = 0;
            for (int i = 0; i < waypoints.size() - 1; i++) {
                LatLng waypoint1 = waypoints.get(i);
                LatLng waypoint2 = waypoints.get(i + 1);
                double x1 = waypoint1.getLongitude();
                double y1 = waypoint1.getLatitude();
                double x2 = waypoint2.getLongitude();
                double y2 = waypoint2.getLatitude();
                sumOfTerms += calculateTerm(x1, y1, x2, y2);
            }

            sumOfTerms += calculateTerm(nextX, y, origin.getLongitude(), origin.getLatitude());

            return sumOfTerms - 2;//4.6 * targetTime / 60;
        };
    }

    private double calculateTerm(double x1, double y1, double x2, double y2) {
        return Math.sqrt(
                111 * Math.pow(y2 - y1, 2) +
                        Math.pow(111 * (x2 - x1) * Math.cos(Math.toRadians(y1)), 2)
        );
    }

    private double getMinX() {
        double x_0 = origin.getLongitude();
        double y_0 = origin.getLatitude();
        double x_last = intermediates.get(intermediates.size() - 1).getLongitude();
        double y_last = intermediates.get(intermediates.size() - 1).getLatitude();
        BrentSolver solver = new BrentSolver(1e-6);
        double x = solver.solve(1000, ellipseDerivative(x_0, y_0, x_last, y_last, 0), -100, -70); // -100과 100 사이에서 x의 값을 찾습니다
        return x;
    }

    private UnivariateFunction ellipseDerivative(double x_0, double y_0, double x_last, double y_last, double y) {
        return x -> {
            double numerator1 = 12321 * Math.pow(Math.cos(Math.toRadians(y_0)), 2) * (x - x_last);
            double denominator1 = Math.sqrt(12321 * Math.pow(Math.cos(Math.toRadians(y_0)), 2) * Math.pow(x - x_last, 2) + 111 * Math.pow(y - y_last, 2));

            double numerator2 = 12321 * Math.pow(Math.cos(Math.toRadians(y_0)), 2) * (x_0 - x);
            double denominator2 = Math.sqrt(12321 * Math.pow(Math.cos(Math.toRadians(y_0)), 2) * Math.pow(x_0 - x, 2) + 111 * Math.pow(y_0 - y, 2));

            return numerator1 / denominator1 - numerator2 / denominator2;
        };
    }
}
