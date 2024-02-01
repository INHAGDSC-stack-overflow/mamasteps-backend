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
public class UserUpdateRequest {

    private String name;
    private Integer age;
    private LocalDateTime pregnancyStartDate;
    private ActivityLevel activityLevel;
    private List<UpdateWalkPreferenceRequest> walkPreferences;
}
