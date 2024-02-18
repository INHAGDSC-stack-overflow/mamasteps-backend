package inhagdsc.mamasteps.map.service;


import inhagdsc.mamasteps.map.dto.*;
import inhagdsc.mamasteps.user.entity.User;

import java.io.IOException;
import java.util.List;

public interface RoutesService {
    public void createRequestProfile(User user);
    public EditRequestProfileResponse editRequestProfile(Long userId, EditRequestProfileRequest routesProfileDto);
    public GetRequestProfileResponse getRequestProfile(Long userId);
    public List<RouteDto> computeRoutes(Long userId) throws IOException;
    void saveRoute(Long userId, RouteDto routeDto);
    List<RouteDto> getRoutes(Long userId);
    void deleteRoute(Long userId, Long routeId);
    void editRouteName(Long userId, Long routeId, String name);
}

