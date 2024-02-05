package inhagdsc.mamasteps.common.converter;

import inhagdsc.mamasteps.user.dto.ChangePasswordResponse;
import inhagdsc.mamasteps.user.dto.ChangeProfileResponse;
import inhagdsc.mamasteps.user.dto.UserInfoResponse;
import inhagdsc.mamasteps.user.dto.WalkPreferenceResponse;
import inhagdsc.mamasteps.user.entity.User;
import inhagdsc.mamasteps.user.entity.WalkPreference;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class UserConverter {

    public static UserInfoResponse toUserResponse(User user) {
        return UserInfoResponse.builder()
                .profileImageUrl(user.getProfileImageUrl())
                .email(user.getEmail())
                .name(user.getName())
                .age(user.getAge())
                .pregnancyStartDate(user.getPregnancyStartDate())
                .guardianPhoneNumber(user.getGuardianPhoneNumber())
                .activityLevel(user.getActivityLevel())
                .walkPreferences(toWalkPreferenceResponses(user.getWalkPreferences()))
                .build();
    }

    private static List<WalkPreferenceResponse> toWalkPreferenceResponses(List<WalkPreference> walkPreferences) {
        return walkPreferences.stream()
                .map(walkPreference -> WalkPreferenceResponse.builder()
                        .dayOfWeek(walkPreference.getDayOfWeek())
                        .startTime(walkPreference.getStartTime())
                        .endTime(walkPreference.getEndTime())
                        .build())
                .collect(Collectors.toList());
    }

    public static ChangePasswordResponse toChangePasswordResponse(User user) {
        return ChangePasswordResponse.builder()
                .newPassword(user.getPassword())
                .build();
    }

    public static ChangeProfileResponse toChangeProfileResponse(User user) {
        return ChangeProfileResponse.builder()
                .newProfileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
