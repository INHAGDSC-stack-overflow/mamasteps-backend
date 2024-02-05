package inhagdsc.mamasteps.auth.controller;

import inhagdsc.mamasteps.auth.dto.*;
import inhagdsc.mamasteps.auth.service.AuthService;
import inhagdsc.mamasteps.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static inhagdsc.mamasteps.common.code.status.SuccessStatus.*;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  private final AuthService authService;

  @PostMapping("/signup")
  public ApiResponse<SignupResponse> signup(@RequestPart("profileImage") MultipartFile profileImage,
                                            @RequestPart("request") SignupRequest request) {
    log.info("signup 호출 {}", request.getEmail());
    return ApiResponse.onSuccess(CREATED, authService.signup(profileImage, request));
  }

  @PostMapping("/login")
  public ApiResponse<LoginReponse> login(@RequestBody LoginRequest request) {
    log.info("login 호출 {}", request.getEmail());
    return ApiResponse.onSuccess(OK, authService.login(request));
  }

  @PostMapping("/refresh-token")
  public ApiResponse<RefreshResponse> refreshToken(HttpServletRequest request, HttpServletResponse response)  {
    return ApiResponse.onSuccess(CREATED, authService.refreshToken(request, response));
  }

  @PostMapping("/google-login")
  public ApiResponse<GoogleLoginResponse> googleLogin(@RequestBody GoogleLoginRequest request) {
    return ApiResponse.onSuccess(OK, authService.googleLogin(request));
  }

  @GetMapping("/health")
  public ApiResponse<String> health() {
    return ApiResponse.onSuccess(OK, "I'm healthy!");
  }



}
