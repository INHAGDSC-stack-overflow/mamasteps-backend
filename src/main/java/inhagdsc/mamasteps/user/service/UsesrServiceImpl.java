package inhagdsc.mamasteps.user.service;


import inhagdsc.mamasteps.common.converter.UserConverter;
import inhagdsc.mamasteps.common.exception.handler.UserHandler;
import inhagdsc.mamasteps.user.dto.UserResponse;
import inhagdsc.mamasteps.user.entity.User;
import inhagdsc.mamasteps.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static inhagdsc.mamasteps.common.code.status.ErrorStatus.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UsesrServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(USER_NOT_FOUND));
        return UserConverter.toUserResponse(user);
    }
}
