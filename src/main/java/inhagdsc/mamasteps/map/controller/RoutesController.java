package inhagdsc.mamasteps.map.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import inhagdsc.mamasteps.common.ApiResponse;
import inhagdsc.mamasteps.common.code.status.SuccessStatus;
import inhagdsc.mamasteps.map.domain.RouteRequestDto;
import inhagdsc.mamasteps.map.service.RoutesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static inhagdsc.mamasteps.common.code.status.SuccessStatus.*;

@RestController
@RequestMapping("/api/v1/routes")
public class RoutesController {

    private final RoutesService routesService;

    @Autowired
    public RoutesController(RoutesService routesService) {
        this.routesService = routesService;
    }

    @PostMapping("/computeRoutes")
    public String getRoutes(@RequestBody RouteRequestDto routeRequestDto) throws IOException, JsonProcessingException {
        String response = routesService.computeRoutes(routeRequestDto);
        return response;
    }
}
