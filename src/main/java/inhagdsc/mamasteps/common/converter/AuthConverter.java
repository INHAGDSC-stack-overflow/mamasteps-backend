package inhagdsc.mamasteps.common.converter;

import inhagdsc.mamasteps.auth.dto.LoginReponse;
import inhagdsc.mamasteps.auth.dto.SignupResponse;
import inhagdsc.mamasteps.user.entity.User;
import inhagdsc.mamasteps.user.entity.enums.ActivityLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class AuthConverter {

    public static SignupResponse toSignupResponse(User user, String accessToken, String refreshToken) {
        return SignupResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .age(user.getAge())
                .pregnancyStartDate(user.getPregnancyStartDate())
                .guardianPhoneNumber(user.getGuardianPhoneNumber())
                .activityLevel(user.getActivityLevel())
                .profileImageUrl(user.getProfileImageUrl())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public static LoginReponse toLoginResponse(User user, String accessToken, String refreshToken) {
        return LoginReponse.builder()
                .userId(user.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
