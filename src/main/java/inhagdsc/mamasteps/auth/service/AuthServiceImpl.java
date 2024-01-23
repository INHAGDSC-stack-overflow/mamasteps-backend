package inhagdsc.mamasteps.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import inhagdsc.mamasteps.auth.dto.LoginReponse;
import inhagdsc.mamasteps.auth.dto.LoginRequest;
import inhagdsc.mamasteps.auth.dto.SignupRequest;
import inhagdsc.mamasteps.auth.dto.SignupResponse;
import inhagdsc.mamasteps.auth.jwt.JwtProvider;
import inhagdsc.mamasteps.auth.redis.RedisProvider;
import inhagdsc.mamasteps.common.converter.AuthConverter;
import inhagdsc.mamasteps.common.exception.handler.UserHandler;
import inhagdsc.mamasteps.user.entity.User;
import inhagdsc.mamasteps.user.entity.enums.Role;
import inhagdsc.mamasteps.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static inhagdsc.mamasteps.auth.jwt.JwtProvider.HEADER_AUTHORIZATION;
import static inhagdsc.mamasteps.auth.jwt.JwtProvider.TOKEN_PREFIX;
import static inhagdsc.mamasteps.common.code.status.ErrorStatus.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtProvider jwtProvider;
  private final RedisProvider redisProvider;

  @Override
  @Transactional
  public SignupResponse signup(SignupRequest request) {
    User user = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .name(request.getName())
            .age(request.getAge())
            .pregnancyStartDate(request.getPregnancyStartDate())
            .profileImageUrl(request.getProfileImageUrl())
            .role(Role.USER)
            .build();
    User savedUser = userRepository.save(user);
    String accessToken = jwtProvider.generateToken(user);
    String refreshToken = jwtProvider.generateRefreshToken(user);
    saveUserToken(savedUser, refreshToken);
    return AuthConverter.toSignupResponse(user, accessToken, refreshToken);
  }

  @Override
  @Transactional
  public LoginReponse login(LoginRequest request) {
    authenticationManager.authenticate
            (new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UserHandler(USER_NOT_FOUND));
    String accessToken = jwtProvider.generateToken(user);
    String refreshToken = jwtProvider.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, refreshToken);
    return AuthConverter.toLoginResponse(user, accessToken, refreshToken);
  }

  private void saveUserToken(User user, String refreshToken) {
    //key는 사용자 이메일과 토큰 발급 시간으로 구성 // 추후에 발급 시간이 아닌 기기로 구분하는 거로 수정해야함
    //redisService.setValueOps(user.getEmail() + ":" + issuedAt, refreshToken);
    redisProvider.setValueOps(user.getEmail(), refreshToken);
    redisProvider.expireValues(user.getEmail());
  }

  private void revokeAllUserTokens(User user) {
    redisProvider.deleteValueOps(user.getEmail());
  }

  @Override
  @Transactional
  public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
    final String authHeader = request.getHeader(HEADER_AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith(TOKEN_PREFIX))
      return;

    refreshToken = authHeader.substring(7);
    userEmail = jwtProvider.extractUsername(refreshToken);
    if (userEmail != null) {
      User user = userRepository.findByEmail(userEmail)
              .orElseThrow(() -> new UserHandler(USER_NOT_FOUND));
      if (jwtProvider.isTokenValid(refreshToken, user)) {
        String accessToken = jwtProvider.generateToken(user);
        SignupResponse signupResponse = SignupResponse.builder()
                .userId(user.getId())
                .accessToken(accessToken)
                .refreshToken(null)
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), signupResponse);
      }
    }
  }
}
