package inhagdsc.mamasteps.map.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import inhagdsc.mamasteps.common.ApiResponse;
import inhagdsc.mamasteps.map.dto.RouteRequestDto;
import inhagdsc.mamasteps.map.domain.RoutesProfileEntity;
import inhagdsc.mamasteps.map.dto.RoutesProfileDto;
import inhagdsc.mamasteps.map.service.RoutesService;
import inhagdsc.mamasteps.user.entity.User;
import inhagdsc.mamasteps.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static inhagdsc.mamasteps.common.code.status.ErrorStatus.FORBIDDEN;
import static inhagdsc.mamasteps.common.code.status.SuccessStatus.CREATED;
import static inhagdsc.mamasteps.common.code.status.SuccessStatus.OK;

@RestController
@RequestMapping("/api/v1/routes")
public class RoutesController {

    private final RoutesService routesService;

    @Autowired
    public RoutesController(RoutesService routesService) {
        this.routesService = routesService;
    }

    @GetMapping("/newProfile/{currentNumber}")
    public ApiResponse<RoutesProfileDto> createProfile(@AuthenticationPrincipal User user, @PathVariable int currentNumber) {
        return ApiResponse.onSuccess(CREATED, routesService.createProfile(user.getId(), currentNumber));
    }

    @GetMapping("/getProfiles")
    public ApiResponse<List<RoutesProfileDto>> getProfiles(@AuthenticationPrincipal User user) {
        List<RoutesProfileDto> response = routesService.getProfiles(user.getId());
        return ApiResponse.onSuccess(OK, response);
    }

    @DeleteMapping("/deleteProfile/{profileId}")
    public ApiResponse<Void> deleteProfile(@AuthenticationPrincipal User user, @PathVariable Long profileId) {
        try {
            routesService.deleteProfile(user.getId(), profileId);
            return ApiResponse.onSuccess(OK, null);
        } catch (Exception e) {
            return ApiResponse.onFailure(FORBIDDEN.getCode(), e.getMessage(), null);
        }
    }

    @PostMapping("/computeRoutes")
    public ApiResponse<ObjectNode> getRoutes(@RequestBody RouteRequestDto routeRequestDto) throws IOException, JsonProcessingException {
        ObjectNode response = routesService.computeRoutes(routeRequestDto);
        return ApiResponse.onSuccess(OK, response);
    }
}