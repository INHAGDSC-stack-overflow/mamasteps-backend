package inhagdsc.mamasteps.user.service;


import inhagdsc.mamasteps.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UsesrServiceImpl implements UserService {

    private final UserRepository userRepository;

}
