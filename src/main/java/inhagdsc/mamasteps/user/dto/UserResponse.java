package inhagdsc.mamasteps.user.dto;

import inhagdsc.mamasteps.user.entity.enums.ActivityLevel;
import inhagdsc.mamasteps.user.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserResponse {

    private String profileImageUrl;
    private String email;
    private String name;
    private Integer age;
    private LocalDateTime pregnancyStartDate;
    private String guardianPhoneNumber;
    private ActivityLevel activityLevel;
}
