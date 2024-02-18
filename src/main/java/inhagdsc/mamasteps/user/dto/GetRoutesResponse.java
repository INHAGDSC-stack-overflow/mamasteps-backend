package inhagdsc.mamasteps.user.dto;

import inhagdsc.mamasteps.map.domain.RouteEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GetRoutesResponse {
    private Long id;
    private String routeName;
    private String polyLine;
    private Double totalDistanceMeters;
    private Integer totalTimeSeconds;
    private String usedAt;
    private int evaluatedHardness;
    private String createdAt;
    private String updatedAt;

    public GetRoutesResponse(RouteEntity routeEntity) {
        this.id = routeEntity.getId();
        this.routeName = routeEntity.getRouteName();
        this.polyLine = routeEntity.getPolyLine();
        this.totalDistanceMeters = routeEntity.getTotalDistanceMeters();
        this.totalTimeSeconds = routeEntity.getTotalTimeSeconds();
        this.usedAt = routeEntity.getUsedAt();
        this.evaluatedHardness = routeEntity.getEvaluatedHardness();
        this.createdAt = routeEntity.getCreatedAt();
        this.updatedAt = routeEntity.getUpdatedAt();
    }
}
