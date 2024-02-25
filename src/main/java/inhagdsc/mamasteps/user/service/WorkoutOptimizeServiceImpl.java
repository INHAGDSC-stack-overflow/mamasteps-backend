package inhagdsc.mamasteps.user.service;

import inhagdsc.mamasteps.common.converter.UserConverter;
import inhagdsc.mamasteps.user.dto.FeedbackTimeRequest;
import inhagdsc.mamasteps.user.dto.OptimizeSpeedRequest;
import inhagdsc.mamasteps.user.dto.WorkoutInfoResponse;
import inhagdsc.mamasteps.user.entity.User;
import inhagdsc.mamasteps.user.entity.enums.ActivityLevel;
import inhagdsc.mamasteps.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class WorkoutOptimizeServiceImpl implements WorkoutOptimizeService {
    private final int FEEDBACK_RADIUS = 9;
    private final int MAX_TARGETTIME = 3600;
    private final int MIN_TARGETTIME = 300;
    private final double MAX_WALKSPEED = 10.0;
    private final double MIN_WALKSPEED = 1.0;

    private final UserRepository userRepository;

    public WorkoutOptimizeServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public WorkoutInfoResponse getWorkoutInfo(Long userId) {
        User user = userRepository.findById(userId).get();
        WorkoutInfoResponse response = new WorkoutInfoResponse();
        response.setTargetTime(user.getTargetTime());
        response.setWalkSpeed(user.getWalkSpeed());
        return response;
    }

    @Override
    public int increaseTargetTime(User user) {
        int targetTime = Math.min(MAX_TARGETTIME, user.getTargetTime() + 60);
        user.setTargetTime(targetTime);
        userRepository.save(user);
        return targetTime;
    }

    @Override
    public void feedbackTime(User user, FeedbackTimeRequest request) {
        user.setTargetTime(
                Math.min(MAX_TARGETTIME,
                        Math.max(MIN_TARGETTIME, (int) (user.getTargetTime() +
                                (user.getTargetTime() * ((double) request.getFeedback() / FEEDBACK_RADIUS) * 45 / 100)))));
        userRepository.save(user);
    }

    @Override
    public void optimizeSpeed(User user, OptimizeSpeedRequest request) {
        double currentSpeed = calculateWalkSpeed(request.getDistanceMeters(), request.getCompleteTimeSeconds());
        double optimizedSpeed = (currentSpeed + user.getWalkSpeed()) / 2;
        if (optimizedSpeed > MAX_WALKSPEED) {
            user.setWalkSpeed(MAX_WALKSPEED);
        }
        else if (optimizedSpeed < MIN_WALKSPEED) {
            user.setWalkSpeed(MIN_WALKSPEED);
        }
        else {
            user.setWalkSpeed(optimizedSpeed);
        }
        userRepository.save(user);
    }

    @Override
    public void initWalkSpeedAndTime(User user, ActivityLevel level) {
        if (level != null) {
            if (level == ActivityLevel.HIGH) {
                user.setWalkSpeed(4.0);
                user.setTargetTime(20 * 60);
            } else if (level == ActivityLevel.MEDIUM) {
                user.setWalkSpeed(3.5);
                user.setTargetTime(15 * 60);
            } else {
                user.setWalkSpeed(3.0);
                user.setTargetTime(10 * 60);
            }
        } else {
            user.setWalkSpeed(3.0);
            user.setTargetTime(10 * 60);
        }
    }

    private double calculateWalkSpeed(int distanceMeters, int completeTimeSeconds) {
        return ((double) distanceMeters / completeTimeSeconds) * 3.6;
    }
}
