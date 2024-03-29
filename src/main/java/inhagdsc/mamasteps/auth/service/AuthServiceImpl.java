package inhagdsc.mamasteps.auth.service;

import inhagdsc.mamasteps.auth.dto.*;
import inhagdsc.mamasteps.auth.jwt.JwtProvider;
import inhagdsc.mamasteps.common.converter.AuthConverter;
import inhagdsc.mamasteps.common.exception.handler.UserHandler;
import inhagdsc.mamasteps.user.entity.User;
import inhagdsc.mamasteps.user.entity.WalkPreference;
import inhagdsc.mamasteps.user.entity.enums.Role;
import inhagdsc.mamasteps.user.repository.UserRepository;
import inhagdsc.mamasteps.user.service.WorkoutOptimizeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import static inhagdsc.mamasteps.common.code.status.ErrorStatus.USER_ALREADY_EXIST;
import static inhagdsc.mamasteps.common.code.status.ErrorStatus.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final WorkoutOptimizeService workoutOptimizeService;
  private final JwtProvider jwtProvider;

  @Override
  public SignupResponse signup(SignupRequest request) {
    User user = createUser(request);
    createWalkPreferences(request, user);
    User savedUser = userRepository.save(user);
    createtoken token = getCreateToken(savedUser);
    return AuthConverter.toSignupResponse(savedUser, token.accessToken());
  }


  @Override
  public LoginReponse login(LoginRequest request) {
    authenticationManager.authenticate
            (new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UserHandler(USER_NOT_FOUND));
    createtoken token = getCreateToken(user);
    return AuthConverter.toLoginResponse(user, token.accessToken);
  }

  @Override
  public GoogleLoginResponse googleLogin(GoogleLoginRequest request) {
    User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UserHandler(USER_NOT_FOUND));
    createtoken token = getCreateToken(user);
    return AuthConverter.toGoogleLoginResponse(user, token.accessToken);
  }

  private User createUser(SignupRequest request) {
    userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
      throw new UserHandler(USER_ALREADY_EXIST);});
    User user = User.builder()
            .email(request.getEmail())
            .name(request.getName())
            .age(request.getAge())
            .pregnancyStartDate(request.getPregnancyStartDate())
            .guardianPhoneNumber(request.getGuardianPhoneNumber())
            .activityLevel(request.getActivityLevel())
            .profileImageUrl(request.getProfileImage())
            .walkCount(0)
            .walkPreferences(new ArrayList<>())
            .role(Role.USER)
            .build();

    workoutOptimizeService.initWalkSpeedAndTime(user, request.getActivityLevel());

    return user;
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

  private record createtoken(String accessToken) { }
  private createtoken getCreateToken(User savedUser) {
    String accessToken = jwtProvider.generateToken(savedUser);
    return new createtoken(accessToken);
  }
  
}
