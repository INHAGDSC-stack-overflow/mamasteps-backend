package inhagdsc.mamasteps.user.service;


import inhagdsc.mamasteps.common.converter.UserConverter;
import inhagdsc.mamasteps.common.exception.handler.UserHandler;
import inhagdsc.mamasteps.user.dto.ChangePasswordRequest;
import inhagdsc.mamasteps.user.dto.ChangePasswordResponse;
import inhagdsc.mamasteps.user.dto.UserResponse;
import inhagdsc.mamasteps.user.entity.User;
import inhagdsc.mamasteps.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static inhagdsc.mamasteps.common.code.status.ErrorStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UsesrServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(USER_NOT_FOUND));
        return UserConverter.toUserResponse(user);
    }

    @Override
    @Transactional
    public ChangePasswordResponse changePassword(ChangePasswordRequest request, User user) {

        log.info("현재 비밀번호 : {}", user.getPassword());
        // 만약 현재 비밀번호가 맞지 않다면 예외
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new UserHandler(PASSWORD_NOT_MATCH);
        }
        // 만약 새로운 비밀번호와 확인 비밀번호가 일치하지 않다면 예외
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new UserHandler(PASSWORD_NOT_MATCH_CONFIRM);
        }
        // 비밀번호 업데이트
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        log.info("비밀번호 변경 완료 : {}", user.getPassword());
        // 새 비밀번호 저장인데 가독성을 위해 작성함. 작성하지 않아도 무방
        userRepository.save(user);
        return UserConverter.toChangePasswordResponse(user);
    }
}
