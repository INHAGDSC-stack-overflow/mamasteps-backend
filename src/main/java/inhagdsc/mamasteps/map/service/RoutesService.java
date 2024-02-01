package inhagdsc.mamasteps.map.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import inhagdsc.mamasteps.map.domain.RouteRequestDto;
import reactor.core.publisher.Mono;

import java.io.IOException;

public interface RoutesService {
    public ObjectNode computeRoutes(RouteRequestDto routeRequestDto) throws IOException;
}

