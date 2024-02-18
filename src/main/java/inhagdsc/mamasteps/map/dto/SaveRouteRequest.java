package inhagdsc.mamasteps.map.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SaveRouteRequest {
    private String polyLine;
    private Double totalDistanceMeters;
    private Integer totalTimeSeconds;
    private String createdAt;
}
