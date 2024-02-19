package inhagdsc.mamasteps.user.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import inhagdsc.mamasteps.common.deserializer.LocalTimeDeserializer;
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
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    private LocalTime startTime;
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    private LocalTime endTime;
    public WalkPreference toEntity() {
        return WalkPreference.builder()
                .dayOfWeek(this.dayOfWeek)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .build();
    }
}
