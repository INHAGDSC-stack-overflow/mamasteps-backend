package inhagdsc.mamasteps.user.dto;

public class OptimizeSpeedRequest {
    private int distanceMeters;
    private int completeTimeSeconds;

    public int getDistanceMeters() {
        return distanceMeters;
    }

    public void setDistanceMeters(int distanceMeters) {
        this.distanceMeters = distanceMeters;
    }

    public int getCompleteTimeSeconds() {
        return completeTimeSeconds;
    }

    public void setCompleteTimeSeconds(int completeTimeSeconds) {
        this.completeTimeSeconds = completeTimeSeconds;
    }
}
