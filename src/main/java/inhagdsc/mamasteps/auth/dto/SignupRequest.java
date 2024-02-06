package inhagdsc.mamasteps.auth.dto;

import inhagdsc.mamasteps.user.entity.enums.ActivityLevel;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SignupRequest {

  @NotBlank
  @Email
  private String email;
  @NotBlank
  private String password;
  @NotBlank
  private String name; // 이름
  @NotNull
  private Integer age; // 나이
  @NotNull
  private LocalDateTime pregnancyStartDate; // 임신 시작일
  @NotBlank
  private String guardianPhoneNumber; // 보호자 전화번호
  @NotNull
  private ActivityLevel activityLevel; // 활동량
  private List<WalkPreferenceRequest> walkPreferences; //산책 선호 시간

}
