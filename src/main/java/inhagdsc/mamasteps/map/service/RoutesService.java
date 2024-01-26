package inhagdsc.mamasteps.map.service;


import inhagdsc.mamasteps.map.domain.RouteRequestDto;
import reactor.core.publisher.Mono;

public interface RoutesService {
    public String computeRoutes(RouteRequestDto routeRequestDto);
}

