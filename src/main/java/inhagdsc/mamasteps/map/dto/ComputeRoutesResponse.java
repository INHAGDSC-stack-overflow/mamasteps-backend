package inhagdsc.mamasteps.map.dto;

import inhagdsc.mamasteps.map.domain.RouteEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ComputeRoutesResponse {
    private String polyLine;
    private Double totalDistanceMeters;
    private Integer totalTimeSeconds;
    private String createdAt;

    public ComputeRoutesResponse(RouteEntity routeEntity) {
        this.polyLine = routeEntity.getPolyLine();
        this.totalDistanceMeters = routeEntity.getTotalDistanceMeters();
        this.totalTimeSeconds = routeEntity.getTotalTimeSeconds();
        this.createdAt = routeEntity.getCreatedAt();
    }
}
