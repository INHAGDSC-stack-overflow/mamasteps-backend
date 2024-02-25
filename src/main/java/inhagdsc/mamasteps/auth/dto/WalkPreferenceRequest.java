package inhagdsc.mamasteps.auth.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import inhagdsc.mamasteps.common.deserializer.LocalTimeDeserializer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class WalkPreferenceRequest {

    private DayOfWeek dayOfWeek;

    @JsonDeserialize(using = LocalTimeDeserializer.class)
    private LocalTime startTime;

    @JsonDeserialize(using = LocalTimeDeserializer.class)
    private LocalTime endTime;
}