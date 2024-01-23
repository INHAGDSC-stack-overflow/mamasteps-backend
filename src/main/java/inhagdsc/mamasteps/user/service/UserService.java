package inhagdsc.mamasteps.user.service;

import inhagdsc.mamasteps.user.dto.UserResponse;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    public UserResponse getUserInfo(Long userId);
}
