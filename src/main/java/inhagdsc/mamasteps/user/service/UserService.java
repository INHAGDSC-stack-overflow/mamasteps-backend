package inhagdsc.mamasteps.user.service;

import inhagdsc.mamasteps.user.dto.ChangePasswordRequest;
import inhagdsc.mamasteps.user.dto.ChangePasswordResponse;
import inhagdsc.mamasteps.user.dto.UserResponse;
import inhagdsc.mamasteps.user.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    public UserResponse getUserInfo(Long userId);
    public ChangePasswordResponse changePassword(ChangePasswordRequest request, User user);
}
