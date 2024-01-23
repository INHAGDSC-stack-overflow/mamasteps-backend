package inhagdsc.mamasteps.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SocialSigninResponse {

    private Long userId;
    private String name;
    private String email;
}
