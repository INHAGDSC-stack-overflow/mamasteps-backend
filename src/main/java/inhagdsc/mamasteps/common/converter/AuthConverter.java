package inhagdsc.mamasteps.common.converter;

import inhagdsc.mamasteps.auth.dto.LoginReponse;
import inhagdsc.mamasteps.auth.dto.RefreshResponse;
import inhagdsc.mamasteps.auth.dto.SignupResponse;
import inhagdsc.mamasteps.user.entity.User;
import inhagdsc.mamasteps.user.entity.enums.ActivityLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class AuthConverter {

    public static SignupResponse toSignupResponse(User user, String accessToken, String refreshToken) {
        return SignupResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public static LoginReponse toLoginResponse(User user, String accessToken, String refreshToken) {
        return LoginReponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public static RefreshResponse toRefreshResponse(User user, String accessToken) {
        return RefreshResponse.builder()
                .userId(user.getId())
                .accessToken(accessToken)
                .build();
    }
}
