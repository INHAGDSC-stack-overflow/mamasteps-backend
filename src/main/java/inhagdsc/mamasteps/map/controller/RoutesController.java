package inhagdsc.mamasteps.map.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

@RestController
@RequestMapping("/api/v1/routes")
public class RoutesController {

    private final RoutesService routesService;

    @Autowired
    public RoutesController(RoutesService routesService) {
        this.routesService = routesService;
    }

    @GetMapping("/newProfile/{currentNumber}")
    public RoutesProfileDto createProfile(@AuthenticationPrincipal User user, @PathVariable int currentNumber) {
        return routesService.createProfile(user.getId(), currentNumber);
    }

    @GetMapping("/getProfiles")
    public List<RoutesProfileDto> getProfiles(@AuthenticationPrincipal User user) {
        List<RoutesProfileDto> response = routesService.getProfiles(user.getId());
        return response;
    }

    @DeleteMapping("/deleteProfile/{profileId}")
    public void deleteProfile(@AuthenticationPrincipal User user, @PathVariable Long profileId) {
        routesService.deleteProfile(user.getId(), profileId);
    }

    @PostMapping("/computeRoutes")
    public ObjectNode getRoutes(@RequestBody RouteRequestDto routeRequestDto) throws IOException, JsonProcessingException {
        ObjectNode response = routesService.computeRoutes(routeRequestDto);
        return response;
    }
}
