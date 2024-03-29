package inhagdsc.mamasteps.map.controller;

import inhagdsc.mamasteps.common.ApiResponse;
import inhagdsc.mamasteps.map.dto.requestProfile.EditRequestProfileRequest;
import inhagdsc.mamasteps.map.dto.requestProfile.EditRequestProfileResponse;
import inhagdsc.mamasteps.map.dto.requestProfile.GetRequestProfileResponse;
import inhagdsc.mamasteps.map.dto.route.ComputeRoutesResponse;
import inhagdsc.mamasteps.map.dto.route.SaveRouteRequest;
import inhagdsc.mamasteps.map.service.RoutesService;
import inhagdsc.mamasteps.user.dto.GetRoutesResponse;
import inhagdsc.mamasteps.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static inhagdsc.mamasteps.common.code.status.ErrorStatus.*;
import static inhagdsc.mamasteps.common.code.status.SuccessStatus.*;

@RestController
@RequestMapping("/api/v1/routes")
public class RoutesController {

    private final RoutesService routesService;

    @Autowired
    public RoutesController(RoutesService routesService) {
        this.routesService = routesService;
    }

    @PostMapping("/createRequestProfile")
    public ApiResponse<Void> createRequestProfile(@AuthenticationPrincipal User user) {
        try {
            routesService.createRequestProfile(user);
            return ApiResponse.onSuccess(CREATED, null);
        } catch (Exception e) {
            return ApiResponse.onFailure(FORBIDDEN.getCode(), e.getMessage(), null);
        }
    }

    @GetMapping("/getRequestProfile")
    public ApiResponse<GetRequestProfileResponse> getRequestProfile(@AuthenticationPrincipal User user) {
        try {
            GetRequestProfileResponse response = routesService.getRequestProfile(user.getId());
            return ApiResponse.onSuccess(OK, response);
        } catch (Exception e) {
            return ApiResponse.onFailure(NOT_FOUND.getCode(), e.getMessage(), null);
        }
    }

    @PatchMapping("/editRequestProfile")
    public ApiResponse<EditRequestProfileResponse> editRequestProfile(@AuthenticationPrincipal User user, @RequestBody EditRequestProfileRequest request) {
        try {
            return ApiResponse.onSuccess(OK, routesService.editRequestProfile(user.getId(), request));
        } catch (Exception e) {
            return ApiResponse.onFailure(FORBIDDEN.getCode(), e.getMessage(), null);
        }
    }

    @PostMapping("/saveRoute")
    public ApiResponse<Void> saveRoute(@AuthenticationPrincipal User user, @RequestBody SaveRouteRequest routeDto) {
        try {
            routesService.saveRoute(user, routeDto);
            return ApiResponse.onSuccess(OK, null);
        } catch (Exception e) {
            return ApiResponse.onFailure(FORBIDDEN.getCode(), e.getMessage(), null);
        }
    }

    @GetMapping("/getRoutes")
    public ApiResponse<List<GetRoutesResponse>> getRoutes(@AuthenticationPrincipal User user) {
        List<GetRoutesResponse> response = routesService.getRoutes(user.getId());
        return ApiResponse.onSuccess(OK, response);
    }

    @PatchMapping("/editRouteName/{routeId}")
    public ApiResponse<Void> editRoute(@AuthenticationPrincipal User user, @PathVariable Long routeId, @RequestBody String name) {
        try {
            routesService.editRouteName(user.getId(), routeId, name);
            return ApiResponse.onSuccess(OK, null);
        } catch (Exception e) {
            return ApiResponse.onFailure(FORBIDDEN.getCode(), e.getMessage(), null);
        }
    }

    @DeleteMapping("/deleteRoute/{routeId}")
    public ApiResponse<Void> deleteRoute(@AuthenticationPrincipal User user, @PathVariable Long routeId) {
        try {
            routesService.deleteRoute(user.getId(), routeId);
            return ApiResponse.onSuccess(OK, null);
        } catch (Exception e) {
            return ApiResponse.onFailure(FORBIDDEN.getCode(), e.getMessage(), null);
        }
    }

    @GetMapping("/computeRoutes")
    public ApiResponse<List<ComputeRoutesResponse>> computeRoutes(@AuthenticationPrincipal User user) throws IOException {
        try {
            List<ComputeRoutesResponse> response = routesService.computeRoutes(user.getId());
            return ApiResponse.onSuccess(OK, response);
        } catch (Exception e) {
            return ApiResponse.onFailure(FORBIDDEN.getCode(), e.getMessage(), null);
        }
    }
}
