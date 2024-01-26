package inhagdsc.mamasteps.map.controller;

import inhagdsc.mamasteps.common.ApiResponse;
import inhagdsc.mamasteps.common.code.status.SuccessStatus;
import inhagdsc.mamasteps.map.domain.RouteRequestDto;
import inhagdsc.mamasteps.map.service.RoutesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static inhagdsc.mamasteps.common.code.status.SuccessStatus.*;

@RestController
@RequestMapping("/api/v1/routes")
public class RoutesController {

    private final RoutesService routesService;

    @Autowired
    public RoutesController(RoutesService routesService) {
        this.routesService = routesService;
    }

//    @PostMapping("/computeRoutes")
//    public Mono<String> getRoutes(@RequestBody RouteRequestDto routeRequestDto) {
//        return routesService.computeRoutes(routeRequestDto);
//    }

//    @PostMapping("/computeRoutes")
//    public ApiResponse<Mono<String>> getRoutes(@RequestBody RouteRequestDto routeRequestDto) {
//        return ApiResponse.onSuccess(CREATED, routesService.computeRoutes(routeRequestDto));
//    }

    @PostMapping("/computeRoutes")
    public Mono<ApiResponse<String>> getRoutes(@RequestBody RouteRequestDto routeRequestDto) {
        return routesService.computeRoutes(routeRequestDto)
                .map(result -> ApiResponse.onSuccess(CREATED, result));
    }

//    @PostMapping("/computeRoutes")
//    public Mono<ApiResponse<String>> getRoutes(@RequestBody RouteRequestDto routeRequestDto) {
//        Mono<String> asd = routesService.computeRoutes(routeRequestDto);
//        Mono<ApiResponse<String>> re = asd.map(result -> ApiResponse.onSuccess(CREATED, result));
//        return re;
//    }

//    @PostMapping("/computeRoutes")
//    public String getRoutes(@RequestBody RouteRequestDto routeRequestDto) {
//        return "asd";
//    }

//    @PostMapping("/computeRoutes")
//    public ApiResponse<Mono<String>> getRoutes(@RequestBody RouteRequestDto routeRequestDto) {
//        ApiResponse<Mono<String>> source = ApiResponse.onSuccess(CREATED,Mono.just("reactor"));
//        //Mono<String> result = source.map(String::toUpperCase);
//        return source;
//    }

    //    @PostMapping("/computeRoutes")
//    public ApiResponse<String> getRoutes(@RequestBody RouteRequestDto routeRequestDto) {
//        ApiResponse<String> response = ApiResponse.onSuccess(CREATED, routesService.computeRoutes(routeRequestDto));
//        return response;
//    }

//    @PostMapping("/computeRoutes")
//    public ApiResponse<Mono<String>> getRoutes(@RequestBody RouteRequestDto routeRequestDto) {
//        return ApiResponse.onSuccess(CREATED, routesService.computeRoutes(routeRequestDto));
//    }
}
