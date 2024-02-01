package inhagdsc.mamasteps.user.dto;

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
public class WalkPreferenceResponse {

    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;


}
