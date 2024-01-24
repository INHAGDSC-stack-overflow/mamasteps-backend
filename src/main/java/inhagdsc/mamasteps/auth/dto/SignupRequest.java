package inhagdsc.mamasteps.auth.dto;

import inhagdsc.mamasteps.user.entity.enums.ActivityLevel;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SignupRequest {

  private String email;
  private String password;
  private String name; // 이름
  private Integer age; // 나이
  private LocalDateTime pregnancyStartDate; // 임신 시작일
  private String guardianPhoneNumber; // 보호자 전화번호
  private ActivityLevel activityLevel; // 활동량

}
