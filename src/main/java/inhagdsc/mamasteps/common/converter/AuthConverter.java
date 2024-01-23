package inhagdsc.mamasteps.common.converter;

import inhagdsc.mamasteps.auth.dto.LoginReponse;
import inhagdsc.mamasteps.auth.dto.SignupResponse;
import inhagdsc.mamasteps.user.entity.User;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class AuthConverter {

    public static SignupResponse toSignupResponse(User user, String accessToken, String refreshToken) {
        return SignupResponse.builder()
                .userId(user.getId())
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
