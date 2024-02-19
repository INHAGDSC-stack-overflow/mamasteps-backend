package inhagdsc.mamasteps.map.service.regional;

import com.fasterxml.jackson.databind.node.ObjectNode;
import inhagdsc.mamasteps.map.domain.RouteRequestProfileEntity;

import java.io.IOException;

public interface RegionalRouteApiService {
    ObjectNode getParsedApiResponse(RouteRequestProfileEntity routeRequestEntity) throws IOException;
}
