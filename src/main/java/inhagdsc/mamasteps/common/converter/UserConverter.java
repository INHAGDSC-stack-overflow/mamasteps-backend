package inhagdsc.mamasteps.common.converter;

import inhagdsc.mamasteps.user.dto.UserResponse;
import inhagdsc.mamasteps.user.entity.User;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class UserConverter {

    public static UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .profileImageUrl(user.getProfileImageUrl())
                .email(user.getEmail())
                .name(user.getName())
                .age(user.getAge())
                .pregnancyStartDate(user.getPregnancyStartDate())
                .guardianPhoneNumber(user.getGuardianPhoneNumber())
                .activityLevel(user.getActivityLevel())
                .build();
    }
}
