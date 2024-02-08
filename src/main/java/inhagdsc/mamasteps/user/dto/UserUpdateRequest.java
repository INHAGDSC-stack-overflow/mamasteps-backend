package inhagdsc.mamasteps.user.dto;

import inhagdsc.mamasteps.user.entity.User;
import inhagdsc.mamasteps.user.entity.enums.ActivityLevel;
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
public class UserUpdateRequest {

    @NotBlank
    private String name;
    @NotNull
    private Integer age;
    @NotNull
    private LocalDateTime pregnancyStartDate;
    @NotNull
    private ActivityLevel activityLevel;
    private List<UpdateWalkPreferenceRequest> walkPreferences;



}
