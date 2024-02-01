package inhagdsc.mamasteps.auth.service;

import inhagdsc.mamasteps.auth.dto.*;
import inhagdsc.mamasteps.auth.jwt.JwtProvider;
import inhagdsc.mamasteps.auth.redis.RedisProvider;
import inhagdsc.mamasteps.common.converter.AuthConverter;
import inhagdsc.mamasteps.common.exception.handler.UserHandler;
import inhagdsc.mamasteps.common.stroge.StorageProvider;
import inhagdsc.mamasteps.user.entity.User;
import inhagdsc.mamasteps.user.entity.WalkPreference;
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
import org.springframework.web.multipart.MultipartFile;


import java.util.ArrayList;

import static inhagdsc.mamasteps.auth.jwt.JwtProvider.HEADER_AUTHORIZATION;
import static inhagdsc.mamasteps.auth.jwt.JwtProvider.TOKEN_PREFIX;
import static inhagdsc.mamasteps.common.code.status.ErrorStatus.USER_NOT_FOUND;
import static inhagdsc.mamasteps.common.stroge.StorageProvider.PROFILE;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtProvider jwtProvider;
  private final RedisProvider redisProvider;
  private final StorageProvider storageProvider;

  @Override
  public SignupResponse signup(MultipartFile profileImage, SignupRequest request) {
    User user = createUser(request, storageProvider.fileUpload(profileImage, PROFILE));
    createWalkPreferences(request, user);
    User savedUser = userRepository.save(user);
    createtoken token = getCreateToken(savedUser);
    return AuthConverter.toSignupResponse(savedUser, token.accessToken(), token.refreshToken());
  }


  @Override
  public LoginReponse login(LoginRequest request) {
    authenticationManager.authenticate
            (new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UserHandler(USER_NOT_FOUND));
    createtoken token = getCreateToken(user);
    return AuthConverter.toLoginResponse(user, token.accessToken, token.refreshToken);
  }

  @Override
  public GoogleLoginResponse googleLogin(GoogleLoginRequest request) {
    User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UserHandler(USER_NOT_FOUND));
    createtoken token = getCreateToken(user);
    return AuthConverter.toGoogleLoginResponse(user, token.accessToken, token.refreshToken);
  }

  @Override
  public RefreshResponse refreshToken(HttpServletRequest request, HttpServletResponse response)  {
    final String authHeader = request.getHeader(HEADER_AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith(TOKEN_PREFIX))
      return null;
    refreshToken = authHeader.substring(7);
    userEmail = jwtProvider.extractUsername(refreshToken);
    if (userEmail != null) {
      User user = userRepository.findByEmail(userEmail)
              .orElseThrow(() -> new UserHandler(USER_NOT_FOUND));
      if (jwtProvider.isTokenValid(refreshToken, user)) {
        String accessToken = jwtProvider.generateToken(user);
        return AuthConverter.toRefreshResponse(user, accessToken);
      }
    }
    return null;
  }
  

  private User createUser(SignupRequest request, String prifleImageUrl) {
    return User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .name(request.getName())
            .age(request.getAge())
            .pregnancyStartDate(request.getPregnancyStartDate())
            .guardianPhoneNumber(request.getGuardianPhoneNumber())
            .activityLevel(request.getActivityLevel())
            .profileImageUrl(prifleImageUrl)
            .walkPreferences(new ArrayList<>())
            .role(Role.USER)
            .build();
  }
  private static void createWalkPreferences(SignupRequest request, User user) {
    if (request.getWalkPreferences() != null) {
      for (WalkPreferenceRequest walkPreferenceRequest : request.getWalkPreferences()) {
        WalkPreference walkPreference = WalkPreference.builder()
                .dayOfWeek(walkPreferenceRequest.getDayOfWeek())
                .startTime(walkPreferenceRequest.getStartTime())
                .endTime(walkPreferenceRequest.getEndTime())
                .build();
        user.addWalkPreference(walkPreference);
      }
    }
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

  private record createtoken(String accessToken, String refreshToken) {
  }
  private createtoken getCreateToken(User savedUser) {
    String accessToken = jwtProvider.generateToken(savedUser);
    String refreshToken = jwtProvider.generateRefreshToken(savedUser);
    revokeAllUserTokens(savedUser);
    saveUserToken(savedUser, refreshToken);
    return new createtoken(accessToken, refreshToken);
  }
  
}
