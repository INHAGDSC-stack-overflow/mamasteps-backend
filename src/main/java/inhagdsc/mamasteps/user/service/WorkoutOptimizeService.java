package inhagdsc.mamasteps.user.service;

import inhagdsc.mamasteps.user.dto.FeedbackTimeRequest;
import inhagdsc.mamasteps.user.dto.OptimizeSpeedRequest;
import inhagdsc.mamasteps.user.dto.WorkoutInfoResponse;
import inhagdsc.mamasteps.user.entity.User;
import inhagdsc.mamasteps.user.entity.enums.ActivityLevel;

public interface WorkoutOptimizeService {
    WorkoutInfoResponse getWorkoutInfo(Long userId);
    int increaseTargetTime(User user);
    void feedbackTime(User user, FeedbackTimeRequest request);
    void initWalkSpeedAndTime(User user, ActivityLevel activityLevel);
    void optimizeSpeed(User user, OptimizeSpeedRequest request);

}
