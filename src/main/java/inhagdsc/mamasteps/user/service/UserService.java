package inhagdsc.mamasteps.user.service;

import inhagdsc.mamasteps.user.dto.ChangePasswordRequest;
import inhagdsc.mamasteps.user.dto.ChangePasswordResponse;
import inhagdsc.mamasteps.user.dto.UserResponse;
import inhagdsc.mamasteps.user.dto.UserUpdateRequest;
import inhagdsc.mamasteps.user.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    UserResponse getUserInfo(Long userId); //개인 정보 조회
    UserResponse updateUserInfo(Long userId, UserUpdateRequest request); //개인 정보 수정
    ChangePasswordResponse changePassword(ChangePasswordRequest request, User user); //비밀번호 변경
}
