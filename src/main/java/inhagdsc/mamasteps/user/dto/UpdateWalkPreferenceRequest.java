package inhagdsc.mamasteps.user.dto;

import inhagdsc.mamasteps.user.entity.WalkPreference;
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
public class UpdateWalkPreferenceRequest {

    private DayOfWeek dayOfWeek;
    private String startTime;
    private String endTime;
    public WalkPreference toEntity() {
        return WalkPreference.builder()
                .dayOfWeek(this.dayOfWeek)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .build();
    }
}
