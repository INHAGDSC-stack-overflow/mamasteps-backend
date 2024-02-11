package inhagdsc.mamasteps.map.service;


import com.fasterxml.jackson.databind.node.ObjectNode;
import inhagdsc.mamasteps.map.dto.RouteRequestDto;
import inhagdsc.mamasteps.map.dto.RoutesProfileDto;

import java.io.IOException;
import java.util.List;

public interface RoutesService {
    public RoutesProfileDto createProfile(Long userId, int currentNumber);
    public List<RoutesProfileDto> getProfiles(Long userId);
    public void editProfile(Long userId, Long profileId, RoutesProfileDto routesProfileDto);
    public void deleteProfile(Long userId, Long profileId);
    public ObjectNode computeRoutes(Long profileId, RouteRequestDto routeRequestDto) throws IOException;
}

