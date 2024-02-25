package inhagdsc.mamasteps.user.controller;

import inhagdsc.mamasteps.common.ApiResponse;
import inhagdsc.mamasteps.user.dto.FeedbackTimeRequest;
import inhagdsc.mamasteps.user.dto.OptimizeSpeedRequest;
import inhagdsc.mamasteps.user.dto.WorkoutInfoResponse;
import inhagdsc.mamasteps.user.entity.User;
import inhagdsc.mamasteps.user.service.WorkoutOptimizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static inhagdsc.mamasteps.common.code.status.SuccessStatus.OK;

@RestController
@RequestMapping("/api/v1/optimize")
@RequiredArgsConstructor
public class WorkoutOptimizeController {
    private final WorkoutOptimizeService workoutOptimizeService;

    @GetMapping("get-info")
    public ApiResponse<WorkoutInfoResponse> getWorkoutInfo(@AuthenticationPrincipal User user) {
        return ApiResponse.onSuccess(OK, workoutOptimizeService.getWorkoutInfo(user.getId()));
    }

    @PostMapping("/optimize-speed")
    public ApiResponse<Void> optimizeWalkSpeed(@AuthenticationPrincipal User user, @RequestBody OptimizeSpeedRequest request) {
        workoutOptimizeService.optimizeSpeed(user, request);
        return ApiResponse.onSuccess(OK, null);
    }

    @PostMapping("/feedback-time")
    public ApiResponse<Void> feedbackTime(@AuthenticationPrincipal User user, @RequestBody FeedbackTimeRequest request) {
        workoutOptimizeService.feedbackTime(user, request);
        return ApiResponse.onSuccess(OK, null);
    }
}
