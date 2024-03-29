package inhagdsc.mamasteps.map.service;


import inhagdsc.mamasteps.map.dto.requestProfile.EditRequestProfileRequest;
import inhagdsc.mamasteps.map.dto.requestProfile.EditRequestProfileResponse;
import inhagdsc.mamasteps.map.dto.requestProfile.GetRequestProfileResponse;
import inhagdsc.mamasteps.map.dto.route.ComputeRoutesResponse;
import inhagdsc.mamasteps.map.dto.route.SaveRouteRequest;
import inhagdsc.mamasteps.user.dto.GetRoutesResponse;
import inhagdsc.mamasteps.user.entity.User;

import java.io.IOException;
import java.util.List;

public interface RoutesService {
    public void createRequestProfile(User user);
    public EditRequestProfileResponse editRequestProfile(Long userId, EditRequestProfileRequest routesProfileDto);
    public GetRequestProfileResponse getRequestProfile(Long userId);
    public List<ComputeRoutesResponse> computeRoutes(Long userId) throws IOException;
    void saveRoute(User user, SaveRouteRequest routeDto);
    List<GetRoutesResponse> getRoutes(Long userId);
    void deleteRoute(Long userId, Long routeId);
    void editRouteName(Long userId, Long routeId, String name);
}

