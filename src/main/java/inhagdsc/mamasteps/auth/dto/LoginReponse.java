package inhagdsc.mamasteps.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LoginReponse {


    @JsonProperty("access_token")
    private String accessToken;
}
