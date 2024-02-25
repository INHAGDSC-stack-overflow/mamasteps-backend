package inhagdsc.mamasteps.common.converter;

import inhagdsc.mamasteps.auth.dto.GoogleLoginResponse;
import inhagdsc.mamasteps.auth.dto.LoginReponse;
import inhagdsc.mamasteps.auth.dto.SignupResponse;
import inhagdsc.mamasteps.user.entity.User;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class AuthConverter {

    public static SignupResponse toSignupResponse(User user, String accessToken) {
        return SignupResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    public static LoginReponse toLoginResponse(User user, String accessToken) {
        return LoginReponse.builder()
                .accessToken(accessToken)
                .build();
    }

    public static GoogleLoginResponse toGoogleLoginResponse(User user, String accessToken) {
        return GoogleLoginResponse.builder()
                .accessToken(accessToken)
                .build();
    }
}
