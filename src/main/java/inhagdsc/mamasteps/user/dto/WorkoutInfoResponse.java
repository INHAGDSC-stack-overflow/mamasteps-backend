package inhagdsc.mamasteps.user.dto;

public class WorkoutInfoResponse {
    private int targetTime;
    private double walkSpeed;

    public int getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(int targetTime) {
        this.targetTime = targetTime;
    }

    public double getWalkSpeed() {
        return walkSpeed;
    }

    public void setWalkSpeed(double walkSpeed) {
        this.walkSpeed = walkSpeed;
    }
}
