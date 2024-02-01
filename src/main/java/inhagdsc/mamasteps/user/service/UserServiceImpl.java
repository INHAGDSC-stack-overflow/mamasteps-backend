package inhagdsc.mamasteps.user.service;


import inhagdsc.mamasteps.common.converter.UserConverter;
import inhagdsc.mamasteps.common.exception.handler.UserHandler;
import inhagdsc.mamasteps.common.stroge.StorageProvider;
import inhagdsc.mamasteps.user.dto.*;
import inhagdsc.mamasteps.user.entity.User;
import inhagdsc.mamasteps.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import static inhagdsc.mamasteps.common.code.status.ErrorStatus.*;
import static inhagdsc.mamasteps.common.stroge.StorageProvider.PROFILE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StorageProvider storageProvider;

    @Override
    public UserInfoResponse getUserInfo(Long userId) {
        User user = userRepository.findUserWithWalkPreferences(userId)
                .orElseThrow(() -> new UserHandler(USER_NOT_FOUND));
        return UserConverter.toUserResponse(user);
    }

    @Override
    @Transactional
    public UserInfoResponse updateUserInfo(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(USER_NOT_FOUND));
        user.updateInfo(request.getName(), request.getAge(), request.getPregnancyStartDate(), request.getActivityLevel() );
        return UserConverter.toUserResponse(user);
    }

    @Override
    @Transactional
    public ChangePasswordResponse changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(USER_NOT_FOUND));
        log.info("현재 비밀번호 : {}", user.getPassword());
        validatePasswordChang(request, user);
        user.setPassword(passwordEncoder.encode(request.getNewPassword())); // 비밀번호 업데이트
        log.info("비밀번호 변경 완료 : {}", user.getPassword());
        userRepository.save(user);  // 새 비밀번호 저장인데 가독성을 위해 작성함. 작성하지 않아도 무방
        return UserConverter.toChangePasswordResponse(user);
    }

    @Override
    @Transactional
    public ChangeProfileResponse updateProfile(Long userId, MultipartFile profileImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(USER_NOT_FOUND));
        deleteFile(user);
        user.updateProfileImageUrl(storageProvider.fileUpload(profileImage, PROFILE));
        return UserConverter.toChangeProfileResponse(user);
    }

    private void validatePasswordChang(ChangePasswordRequest request, User user) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword()))
            throw new UserHandler(PASSWORD_NOT_MATCH);// 만약 현재 비밀번호가 맞지 않다면 예외
        if (!request.getNewPassword().equals(request.getConfirmationPassword()))
            throw new UserHandler(PASSWORD_NOT_MATCH_CONFIRM); // 만약 새로운 비밀번호와 확인 비밀번호가 일치하지 않다면 예외
    }

    //이거 예외처리 어떻게할지 고민해봐야할듯
    private void deleteFile(User user) {
        boolean deleteFile = storageProvider.deleteFile(user.getProfileImageUrl());
        if(deleteFile) {
            log.info("기존 프로필 이미지 삭제 완료");
        } else {
            log.info("기존 프로필 이미지 삭제 실패");
        }
    }
}
