package inhagdsc.mamasteps.user.dto;

import inhagdsc.mamasteps.user.entity.enums.ActivityLevel;
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
public class UserInfoResponse {

    private String profileImageUrl;
    private String email;
    private String name;
    private Integer age;
    private LocalDateTime pregnancyStartDate;
    private String guardianPhoneNumber;
    private ActivityLevel activityLevel;
    private List<WalkPreferenceResponse> walkPreferences;
}
