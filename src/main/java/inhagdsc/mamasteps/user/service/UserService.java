package inhagdsc.mamasteps.user.service;

import inhagdsc.mamasteps.user.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface UserService {

    UserResponse getUserInfo(Long userId); //개인 정보 조회
    UserResponse updateUserInfo(Long userId, UserUpdateRequest request); //개인 정보 수정
    ChangePasswordResponse changePassword(Long userId, ChangePasswordRequest request); //비밀번호 변경

    ChangeProfileResponse updateProfile(Long userId, MultipartFile profileImage);
}
