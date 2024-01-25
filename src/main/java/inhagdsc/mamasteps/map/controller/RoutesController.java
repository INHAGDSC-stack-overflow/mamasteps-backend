package inhagdsc.mamasteps.map.controller;

import inhagdsc.mamasteps.common.ApiResponse;
import inhagdsc.mamasteps.map.domain.RouteRequestDto;
import inhagdsc.mamasteps.map.service.RoutesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class RoutesController {

    private final RoutesService routesService;

    @Autowired
    public RoutesController(RoutesService routesService) {
        this.routesService = routesService;
    }

    @PostMapping("/computeRoutes")
    public ApiResponse<Mono<String>> getRoutes(@RequestBody RouteRequestDto routeRequestDto) {
        return ApiResponse.onSuccess(routesService.computeRoutes(routeRequestDto));
    }
}
