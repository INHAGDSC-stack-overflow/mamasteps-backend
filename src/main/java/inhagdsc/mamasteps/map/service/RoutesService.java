package inhagdsc.mamasteps.map.service;


import com.fasterxml.jackson.databind.node.ObjectNode;
import inhagdsc.mamasteps.map.dto.RouteRequestDto;
import inhagdsc.mamasteps.map.dto.RoutesProfileDto;

import java.io.IOException;

public interface RoutesService {
    public RoutesProfileDto createProfile(Long userId, int currentNumber);
    public ObjectNode computeRoutes(RouteRequestDto routeRequestDto) throws IOException;

}

