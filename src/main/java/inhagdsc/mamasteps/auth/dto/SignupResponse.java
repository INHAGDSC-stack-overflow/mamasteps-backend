package inhagdsc.mamasteps.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import inhagdsc.mamasteps.user.entity.enums.ActivityLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SignupResponse {

  @JsonProperty("access_token")
  private String accessToken;
}
