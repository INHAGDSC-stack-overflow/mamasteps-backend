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

  private Long userId;
  private String name; // 이름
  private Integer age; // 나이
  private LocalDateTime pregnancyStartDate; // 임신 시작일
  private String guardianPhoneNumber; // 보호자 전화번호
  private ActivityLevel activityLevel; // 활동량
  private String profileImageUrl; // 프로필 이미지 URL
  @JsonProperty("access_token")
  private String accessToken;
  @JsonProperty("refresh_token")
  private String refreshToken;
}
