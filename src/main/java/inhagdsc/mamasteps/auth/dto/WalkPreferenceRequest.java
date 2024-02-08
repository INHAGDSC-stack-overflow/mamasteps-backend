package inhagdsc.mamasteps.auth.dto;

import inhagdsc.mamasteps.user.entity.enums.DayOfWeek;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class WalkPreferenceRequest {

    private DayOfWeek dayOfWeek;
    private String startTime;
    private String endTime;
}
